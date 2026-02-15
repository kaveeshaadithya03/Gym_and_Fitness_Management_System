from flask import Flask, request, jsonify, render_template_string
from flask_cors import CORS
import mysql.connector
from mysql.connector import Error
import re
import logging

app = Flask(__name__)
CORS(app)  # Allows requests from chatbot.html (localhost/live server)

# Logging setup
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def get_db_connection():
    try:
        conn = mysql.connector.connect(
            host="127.0.0.1",
            port=3306,
            user="root",
            password="Kaveesha@2026",
            database="gym",
            raise_on_warnings=True
        )
        return conn
    except Error as e:
        logger.error(f"DB connection failed: {e}")
        return None

def update_chatbot_activity(email):
    """Update chatbot session activity"""
    conn = get_db_connection()
    if conn:
        try:
            cursor = conn.cursor()
            sql = "UPDATE chatbot_sessions SET last_activity = NOW() WHERE user_email = ? AND is_active = 1"
            cursor.execute(sql, (email.lower(),))
            conn.commit()
        except Exception as e:
            logger.error(f"Error updating chatbot activity: {e}")
        finally:
            if conn.is_connected():
                cursor.close()
                conn.close()

def end_chatbot_session(email):
    """End chatbot session for user"""
    conn = get_db_connection()
    if conn:
        try:
            cursor = conn.cursor()
            sql = "UPDATE chatbot_sessions SET is_active = 0 WHERE user_email = ?"
            cursor.execute(sql, (email.lower(),))
            conn.commit()
        except Exception as e:
            logger.error(f"Error ending chatbot session: {e}")
        finally:
            if conn.is_connected():
                cursor.close()
                conn.close()

def validate_data(data):
    errors = []

    name = data.get('name', '').strip()
    if not name or len(name) < 2:
        errors.append("Name is required (min 2 characters)")

    try:
        age = int(data.get('age'))
        if not 16 <= age <= 100:
            errors.append("Age must be between 16 and 100")
    except (ValueError, TypeError):
        errors.append("Age must be a valid number")

    contact = data.get('contact_number', '').strip()
    if not re.match(r'^\+?\d{9,15}$', contact):
        errors.append("Contact number must be valid (9–15 digits, optional +)")

    email = data.get('email', '').strip().lower()
    if not re.match(r'^[\w\.-]+@[\w\.-]+\.\w+$', email):
        errors.append("Invalid email format")

    gender = data.get('gender', '').strip().lower()
    if gender not in ['male', 'female', 'other']:
        errors.append("Gender must be Male, Female or Other")

    payment_method = data.get('payment_method', '').strip().lower()
    transaction_id = data.get('transaction_id', '').strip()
    if payment_method == 'online' and not transaction_id:
        errors.append("Transaction ID is required for online payments")

    return errors

@app.route('/chatbot-activity', methods=['POST'])
def update_activity():
    """Update chatbot session activity"""
    data = request.get_json()
    if not data or 'email' not in data:
        return jsonify({"error": "Email is required"}), 400
    
    update_chatbot_activity(data['email'])
    return jsonify({"success": True}), 200

@app.route('/register', methods=['POST'])
def register_member():
    data = request.get_json()
    if not data:
        return jsonify({"error": "No data provided"}), 400

    validation_errors = validate_data(data)
    if validation_errors:
        return jsonify({"error": "Validation failed", "details": validation_errors}), 400

    conn = get_db_connection()
    if not conn:
        return jsonify({"error": "Database connection failed"}), 500

    try:
        cursor = conn.cursor()

        # No CREATE TABLE here anymore – assuming table already exists

        insert_query = """
            INSERT INTO members 
            (name, age, contact_number, email, gender, payment_method, transaction_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        values = (
            data['name'].strip(),
            int(data['age']),
            data['contact_number'].strip(),
            data['email'].strip().lower(),
            data['gender'].capitalize(),
            data.get('payment_method'),
            data.get('transaction_id')
        )

        cursor.execute(insert_query, values)
        conn.commit()

        member_id = cursor.lastrowid

        # End chatbot session after successful registration
        end_chatbot_session(data['email'])

        return jsonify({
            "success": True,
            "message": "Member registered successfully",
            "member_id": member_id
        }), 201

    except mysql.connector.IntegrityError as e:
        conn.rollback()
        if "Duplicate entry" in str(e):
            # End session even if registration fails due to duplicate
            end_chatbot_session(data.get('email', ''))
            return jsonify({"error": "This email is already registered"}), 409
        logger.error(f"Integrity error: {e}")
        return jsonify({"error": "Database integrity error"}), 500

    except mysql.connector.Error as e:
        conn.rollback()
        logger.error(f"Database error: {e}")
        return jsonify({"error": f"Database error: {str(e)}"}), 500

    except Exception as e:
        conn.rollback()
        logger.error(f"Unexpected error: {e}")
        return jsonify({"error": f"Unexpected error: {str(e)}"}), 500

    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

# Success page (shown after successful registration)
SUCCESS_HTML = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Registration Successful</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f0f2f5;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #333;
        }
        .card {
            background: white;
            padding: 50px 40px;
            border-radius: 12px;
            box-shadow: 0 8px 30px rgba(0,0,0,0.15);
            text-align: center;
            max-width: 500px;
        }
        h1 { color: #27ae60; margin-bottom: 20px; }
        a {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 30px;
            background: #2c3e50;
            color: white;
            text-decoration: none;
            border-radius: 30px;
            font-weight: bold;
        }
        a:hover { background: #34495e; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Registration Successful!</h1>
        <p>Welcome to Gym Fitness Management System</p>
        <p>Your account has been created successfully.</p>
        <p>Member ID: {{ member_id }}</p>
        <a href="http://localhost:8081/member/dashboard">Back to Home</a>
    </div>
</body>
</html>
"""

@app.route('/success/<int:member_id>')
def success_page(member_id):
    return render_template_string(SUCCESS_HTML, member_id=member_id)

if __name__ == '__main__':
    app.run(debug=True, port=5001, host='0.0.0.0')
    
#sql query
#CREATE TABLE members (
#    id INT AUTO_INCREMENT PRIMARY KEY,
#    name VARCHAR(255) NOT NULL,
#    age INT NOT NULL,
#   contact_number VARCHAR(20) NOT NULL,
#    email VARCHAR(255) NOT NULL UNIQUE,
#    gender VARCHAR(10) NOT NULL,
#    payment_method VARCHAR(20),
#    transaction_id VARCHAR(100),
#    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
#);