from flask import Flask, request, jsonify
from flask_cors import CORS
import mysql.connector
from mysql.connector import Error
import logging

app = Flask(__name__)
CORS(app)

# ─── Logging ────────────────────────────────────────────────────────────
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ─── Database Connection ────────────────────────────────────────────────
def get_db_connection():
    try:
        conn = mysql.connector.connect(
            host="127.0.0.1",
            port=3306,
            user="root",
            password="Kaveesha@2026",
            database="api",               # ← your latest database name
            raise_on_warnings=True
        )
        return conn
    except Error as e:
        logger.error(f"Database connection failed: {e}")
        return None

# ─── BMI Category Calculation ───────────────────────────────────────────
def calculate_bmi_category(height_cm: float, weight_kg: float) -> str:
    if height_cm <= 0 or weight_kg <= 0:
        return "invalid"
    bmi = weight_kg / ((height_cm / 100) ** 2)
    if bmi < 18.5:
        return "Underweight"
    elif bmi < 25:
        return "Normal weight"
    elif bmi < 30:
        return "Overweight"
    else:
        return "Obesity"

# ─── Debug endpoint – check database connection & data ──────────────────
@app.route("/api/debug-connection", methods=["GET"])
def debug_connection():
    conn = get_db_connection()
    if not conn:
        return jsonify({"status": "error", "message": "Cannot connect to database"}), 500

    try:
        cursor = conn.cursor(dictionary=True)
        cursor.execute("SELECT DATABASE() as db")
        db_name = cursor.fetchone()["db"]

        cursor.execute("SELECT COUNT(*) as total FROM plans")
        total = cursor.fetchone()["total"]

        cursor.execute("SELECT * FROM plans LIMIT 5")
        sample = cursor.fetchall()

        return jsonify({
            "status": "ok",
            "connected_database": db_name,
            "plans_table_rows": total,
            "sample_rows": sample
        })
    except Error as e:
        return jsonify({"status": "error", "message": str(e)}), 500
    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

# ─── Main recommendation endpoint (with BMI calculation + fallbacks) ────
@app.route("/api/recommend-plan", methods=["POST"])
def recommend_plan():
    data = request.get_json(silent=True) or {}

    # Required fields
    if "gender" not in data or "goal" not in data:
        return jsonify({"success": False, "message": "Missing gender or goal"}), 400

    # ─── Input normalization ────────────────────────────────────────────
    gender = str(data["gender"]).strip().title()
    goal   = str(data["goal"]).strip().lower()

    # BMI calculation (if height & weight provided)
    bmi_category = None
    if "height_cm" in data and "weight_kg" in data:
        try:
            height = float(data["height_cm"])
            weight = float(data["weight_kg"])
            bmi_category = calculate_bmi_category(height, weight)
        except Exception:
            return jsonify({"success": False, "message": "Invalid height_cm or weight_kg"}), 400
    elif "bmi_category" in data:
        bmi_category = str(data["bmi_category"]).strip().title()

    if not bmi_category:
        return jsonify({"success": False, "message": "BMI category or height+weight required"}), 400

    conn = get_db_connection()
    if not conn:
        return jsonify({"success": False, "message": "Database connection failed"}), 500

    try:
        cursor = conn.cursor(dictionary=True)

        # 1. Exact match
        cursor.execute("""
            SELECT `Exercise Schedule`, `Meal Plan`, `BMI Category`, `Gender`
            FROM plans
            WHERE LOWER(TRIM(`Gender`)) = LOWER(%s)
              AND LOWER(TRIM(`Goal`)) = LOWER(%s)
              AND LOWER(TRIM(`BMI Category`)) = LOWER(%s)
            LIMIT 1
        """, (gender, goal, bmi_category))

        row = cursor.fetchone()

        if row:
            return jsonify({
                "success": True,
                "match_type": "exact",
                "gender": gender,
                "goal": goal,
                "bmi_category": row["BMI Category"],
                "exercise_schedule": row["Exercise Schedule"],
                "meal_plan": row["Meal Plan"],
                "disclaimer": "This is general guidance only. Consult a doctor or trainer."
            })

        # 2. Fallback: same goal + bmi (ignore gender)
        cursor.execute("""
            SELECT `Gender`, `Exercise Schedule`, `Meal Plan`, `BMI Category`
            FROM plans
            WHERE LOWER(TRIM(`Goal`)) = LOWER(%s)
              AND LOWER(TRIM(`BMI Category`)) = LOWER(%s)
            LIMIT 1
        """, (goal, bmi_category))

        row = cursor.fetchone()
        if row:
            return jsonify({
                "success": True,
                "match_type": "gender_fallback",
                "note": f"No exact match for {gender}. Showing plan for {row['Gender']}.",
                "gender": row["Gender"],
                "goal": goal,
                "bmi_category": row["BMI Category"],
                "exercise_schedule": row["Exercise Schedule"],
                "meal_plan": row["Meal Plan"],
                "disclaimer": "This is general guidance only. Consult a doctor or trainer."
            })

        # 3. Last fallback: any plan with same goal
        cursor.execute("""
            SELECT `Gender`, `BMI Category`, `Exercise Schedule`, `Meal Plan`
            FROM plans
            WHERE LOWER(TRIM(`Goal`)) = LOWER(%s)
            LIMIT 1
        """, (goal,))

        row = cursor.fetchone()
        if row:
            return jsonify({
                "success": True,
                "match_type": "broad_fallback",
                "note": "Showing a general plan for this goal (no close match found).",
                "gender": row["Gender"],
                "bmi_category": row["BMI Category"],
                "exercise_schedule": row["Exercise Schedule"],
                "meal_plan": row["Meal Plan"],
                "disclaimer": "This is general guidance only. Consult a doctor or trainer."
            })

        # No match
        return jsonify({
            "success": False,
            "message": "No matching plan found",
            "debug_info": {
                "searched_gender": gender,
                "searched_goal": goal,
                "searched_bmi": bmi_category
            }
        }), 404

    except Error as e:
        logger.error(f"Query error: {e}")
        return jsonify({"success": False, "message": f"Database error: {str(e)}"}), 500

    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

# ─── List all plans (admin/debug) ───────────────────────────────────────
@app.route("/api/plans", methods=["GET"])
def get_all_plans():
    conn = get_db_connection()
    if not conn:
        return jsonify({"success": False, "message": "Database connection failed"}), 500

    try:
        cursor = conn.cursor(dictionary=True)
        cursor.execute("""
            SELECT `Gender`, `Goal`, `BMI Category`,
                   `Exercise Schedule`, `Meal Plan`
            FROM plans
            ORDER BY `Gender`, `Goal`, `BMI Category`
        """)
        plans = cursor.fetchall()

        return jsonify({
            "success": True,
            "count": len(plans),
            "plans": plans
        })
    except Error as e:
        logger.error(f"Error fetching plans: {e}")
        return jsonify({"success": False, "message": str(e)}), 500
    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

# ─── Simple health check ────────────────────────────────────────────────
@app.route("/api/health", methods=["GET"])
def health_check():
    conn = get_db_connection()
    status = "healthy" if conn and conn.is_connected() else "database connection failed"
    if conn and conn.is_connected():
        conn.close()
    return jsonify({"status": status, "service": "gym-plan-api"})

# ─── General free-text question answering ───────────────────────────────
@app.route("/api/ask-question", methods=["POST"])
def ask_question():
    data = request.get_json(silent=True) or {}
    question = data.get("question", "").strip()
    if not question:
        return jsonify({"success": False, "message": "Missing 'question' field"}), 400

    q_lower = question.lower().strip()
    conn = get_db_connection()
    if not conn:
        return jsonify({"success": False, "message": "Database connection failed"}), 500

    try:
        cursor = conn.cursor(dictionary=True)
        answer = None
        category = "general"
        disclaimer = "<br><br><small><i>This is general information only. Consult a doctor or certified trainer for personalized advice.</i></small>"

        # Simple rule-based answers
        if any(g in q_lower for g in ['hi', 'hello', 'hey', 'sup', 'good morning', 'good evening']):
            answer = "Hey there! 👋 How can I help you with your fitness or nutrition today?"
        elif any(p in q_lower for p in ['muscle gain', 'gain muscle', 'build muscle', 'muscle building', 'bulk', 'mass gain', 'hypertrophy']):
            answer = "For muscle gain, aim for 1.6–2.2 g of protein per kg of body weight daily. Focus on progressive overload in training and a slight calorie surplus. Good protein sources: chicken breast, eggs, salmon, Greek yogurt, cottage cheese, whey protein, lentils, lean beef."
        elif any(p in q_lower for p in ['fat loss', 'lose fat', 'burn fat', 'weight loss', 'cut', 'shred']):
            answer = "To lose fat, create a moderate calorie deficit (300–500 kcal below maintenance). Prioritize high protein (1.8–2.4 g/kg), plenty of vegetables, and moderate carbs & fats. Strength training + cardio helps preserve muscle."
        elif any(p in q_lower for p in ['meal', 'nutrition', 'diet', 'food', 'eat', 'eating plan']):
            answer = "A good meal plan depends on your goal. Muscle gain → high protein + carbs. Fat loss → high protein + veggies + moderate carbs. Want a more specific suggestion? Tell me your gender, goal and BMI category!"
        elif any(p in q_lower for p in ['workout', 'exercise', 'routine', 'plan', 'training', 'gym plan']):
            answer = "A solid beginner routine is full-body training 3× per week: squats, push-ups, rows, deadlifts (light), planks, glute bridges. Focus on good form and progressive overload."
        elif any(p in q_lower for p in ['back pain', 'lower back', 'knee pain', 'shoulder pain', 'injury', 'pain gym']):
            answer = "Gym-related pain (especially lower back) is often caused by poor form, weak core, or rushing heavy lifts. Tips: learn proper bracing, strengthen core & glutes, avoid rounding your back, work on mobility. If pain continues, see a physiotherapist."
        elif 'protein' in q_lower and ('how much' in q_lower or 'per day' in q_lower):
            answer = "Active people / muscle building: 1.6–2.2 g per kg body weight. General health: 1.2–1.6 g/kg is usually enough. Example: 80 kg person → 130–180 g protein daily for muscle growth."
        else:
            # Optional: search in a questions table (if you have one)
            pass

        if answer:
            return jsonify({
                "success": True,
                "answer": answer + disclaimer,
                "category": category
            })

        return jsonify({
            "success": True,
            "answer": "Sorry, I don't have a specific answer for that question yet.<br><br>Try asking about:<br>• meal plans<br>• muscle gain<br>• fat loss<br>• workout routines<br>• gym injuries / pain" + disclaimer,
            "category": "general"
        })

    except Error as e:
        logger.error(f"Question error: {e}")
        return jsonify({"success": False, "message": "Database error occurred"}), 500
    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

# ─── Start the server ───────────────────────────────────────────────────
if __name__ == "__main__":
    print("Starting Gym & Fitness API...")
    print("Endpoints:")
    print("  GET  /api/health")
    print("  GET  /api/debug-connection")
    print("  GET  /api/plans")
    print("  POST /api/recommend-plan")
    print("  POST /api/ask-question")
    app.run(debug=True, host="0.0.0.0", port=5000)