-- Update availabilities table to support both trainers and doctors
-- Make trainer_id nullable and add doctor_id column

-- Step 1: Make trainer_id nullable
ALTER TABLE availabilities MODIFY COLUMN trainer_id BIGINT NULL;

-- Step 2: Add doctor_id column (nullable)
ALTER TABLE availabilities ADD COLUMN IF NOT EXISTS doctor_id BIGINT NULL;

-- Step 3: Add foreign key constraint for doctor_id
ALTER TABLE availabilities 
ADD CONSTRAINT fk_availability_doctor 
FOREIGN KEY (doctor_id) REFERENCES doctor_profiles(id) 
ON DELETE CASCADE;

-- Step 4: Add is_booked column with default value
ALTER TABLE availabilities ADD COLUMN IF NOT EXISTS is_booked BOOLEAN NOT NULL DEFAULT FALSE;

-- Step 5: Add check constraint to ensure either trainer_id or doctor_id is set (but not both)
-- Note: MySQL doesn't support CHECK constraints well in older versions, so this is optional
-- ALTER TABLE availabilities ADD CONSTRAINT chk_trainer_or_doctor 
-- CHECK ((trainer_id IS NOT NULL AND doctor_id IS NULL) OR (trainer_id IS NULL AND doctor_id IS NOT NULL));

-- Verify the changes
DESCRIBE availabilities;
