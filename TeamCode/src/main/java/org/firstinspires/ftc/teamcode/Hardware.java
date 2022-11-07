package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;



public class Hardware {
    //Counts per 1 motor encoder revolution//
    public static final int cpr = 1680;
    //Counts per inch of linear travel - cpi = cpr /("wheel diameter" * Math.PI //
    public static final double cpi = cpr / (4 * Math.PI);
    //Motor initialization - Separate from .xml configuration and is used later on to reference the configuration, "DCMotorEx adds" extra motor conventions over "DCMotorSimple" or "DCMotor"//
    public final DcMotorEx frontRight, backRight, backLeft, frontLeft, lift, intake, turner;
    //Distance Sensor initialization - Separate from .xml configuration and is used later on to reference the configuration//
    //private final DistanceSensor distanceLeft, distanceRight;
    //Touch Sensor initialization - Separate from .xml configuration and is used later on to reference the configuration, "TouchSensor" outputs boolean compared to "DigitalChannel"//
    private final TouchSensor liftTouch;
    //IMU Sensor initialization - Separate from .xml configuration and is used later on to reference the configuration//
    //private final BNO055IMU imu;
    //Variable Setup - Initialize any needed variables here//
    String status = null;

    //Controller Setup = is needed if the drive functions are not in the main file (TeleOp)//
    float right_stick_y, left_stick_y, right_stick_x, left_stick_x, right_trigger, left_trigger;
    boolean left_bumper, right_bumper, a, b, x, y, dpad_right, dpad_left, dpad_up, dpad_down;
    double deadZoneDriveX, deadZoneDriveY, deadZoneDriveRotate;
    double deadZoneRT = 0, deadZoneLT = 0, deadZoneA, deadZoneB, deadZoneX, deadZoneY;

    //Hardware Map - Associates the physical hardware devices (assigned in the configuration) to the software definitions (DCMotorEx, DigitalChannel, etc.) without referencing each port and IO Mapping again//
    public Hardware(@NonNull HardwareMap map) {
        frontRight = map.get(DcMotorEx.class, "MotorC0");
        frontRight.setDirection(DcMotorEx.Direction.FORWARD);
        backRight = map.get(DcMotorEx.class, "MotorC1");
        backRight.setDirection(DcMotorEx.Direction.FORWARD);
        backLeft = map.get(DcMotorEx.class, "MotorC2");
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        frontLeft = map.get(DcMotorEx.class, "MotorC3");
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        lift = map.get(DcMotorEx.class, "MotorE0");
        lift.setDirection(DcMotorEx.Direction.REVERSE);
        intake = map.get(DcMotorEx.class, "MotorE1");
        intake.setDirection(DcMotorEx.Direction.FORWARD);
        turner = map.get(DcMotorEx.class, "MotorE2");
        turner.setDirection(DcMotorEx.Direction.FORWARD);
        liftTouch = map.get(TouchSensor.class, "Touch1");
    }

    //Custom Functions//

    //Mecchanum Drive Calculations//
    public void mecchanumDriveCalc(double speed) {
        if (Math.abs(left_stick_x) < 0.05) {
            deadZoneDriveX = 0;
        } else {
            deadZoneDriveX = -left_stick_x;
        }
        if (Math.abs(left_stick_y) < 0.05) {
            deadZoneDriveY = 0;
        } else {
            deadZoneDriveY = left_stick_y;
        }
        if (Math.abs(right_stick_x) < 0.05) {
            deadZoneDriveRotate = 0;
        } else {
            deadZoneDriveRotate = right_stick_x;
        }
        double r = Math.hypot(deadZoneDriveX, -deadZoneDriveY);
        double robotAngle = Math.atan2(-deadZoneDriveY, deadZoneDriveX) - Math.PI / 4;
        double rightX = deadZoneDriveRotate / 1.25;
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;
        frontRight.setPower(v1 * speed);
        frontLeft.setPower(v2 * speed);
        backRight.setPower(v3 * speed);
        backLeft.setPower(v4 * speed);
    }

    //Basic 4WD Calculations//
    public void normalDriveOp(double driveSpeed) {
        if (Math.abs(right_stick_x) > .05) {
            deadZoneDriveX = right_stick_x;
        } else if (Math.abs(left_stick_y) > .05) {
            deadZoneDriveY = left_stick_y;
        } else {
            deadZoneDriveX = 0;
            deadZoneDriveY = 0;
        }
        final double left = deadZoneDriveX + deadZoneDriveY;
        final double right = deadZoneDriveX - deadZoneDriveY;
        frontLeft.setPower(left * driveSpeed);
        backLeft.setPower(left * driveSpeed);
        frontRight.setPower(right * driveSpeed);
        backRight.setPower(right * driveSpeed);
    }

    //Carousel Turner Op//
    public void turnOp(double speed) {
        if (b) {
            deadZoneB = 1;
            deadZoneX = 0;
        } else if (x) {
            deadZoneX = -1;
            deadZoneB = 0;
        } else {
            deadZoneB = 0;
            deadZoneX = 0;
        }
        final double v1 = deadZoneB + deadZoneX;
        turner.setPower(v1 * speed);
    }

    //Intake Op//
    public void intakeOp(double speed) {
        if (a) {
            deadZoneA = 1;
            deadZoneY = 0;
        } else if (y) {
            deadZoneA = -1;
            deadZoneY = 0;
        } else {
            deadZoneA = 0;
            deadZoneY = 0;
        }
        final double v1 = deadZoneA + deadZoneY;
        turner.setPower(v1 * speed);
    }

    //Lift Op//
    public void liftOp(double speed) {
        int topPos = 1600;
        resetLift();
        if (right_trigger > .05 && lift.getCurrentPosition() < topPos) {
            deadZoneRT = right_trigger;
            deadZoneLT = 0;
        } else if (left_trigger > .05 && lift.getCurrentPosition() > 0) {
            deadZoneLT = -left_trigger;
            deadZoneRT = 0;
        } else if (lift.getCurrentPosition() < 0 && lift.getCurrentPosition() > topPos) {
            resetLift();
        }
        final double v1 = deadZoneLT + deadZoneRT;
        intake.setPower(v1 * speed);
    }

    //Autonomous Functions using Encoders (Self Explanatory)//
    public void goForward(double speed, int in) {
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(target);
        backRight.setTargetPosition(target);
        backLeft.setTargetPosition(target);
        frontLeft.setTargetPosition(target);
        frontRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
        frontLeft.setPower(speed);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            status = "Forwards";
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }

    public void goBackward(double speed, int in) {
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(target);
        backRight.setTargetPosition(target);
        backLeft.setTargetPosition(target);
        frontLeft.setTargetPosition(target);
        frontRight.setPower(-speed);
        backRight.setPower(-speed);
        backLeft.setPower(-speed);
        frontLeft.setPower(-speed);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            status = "Backwards";
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }
    public void intake(double speed, int in) {
        intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        intake.setTargetPosition(target);
        intake.setPower(speed);
        while (intake.isBusy()) {
            status = "intake";
        }
        intake.setPower(0);
    }

    public void strafeLeft(double speed, int in) {
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(target);
        backRight.setTargetPosition(target);
        backLeft.setTargetPosition(target);
        frontLeft.setTargetPosition(target);
        frontRight.setPower(speed);
        backRight.setPower(-speed);
        backLeft.setPower(speed);
        frontLeft.setPower(-speed);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            status = "Strafing Left";
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }

    public void strafeRight(double speed, int in) {
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(target);
        backRight.setTargetPosition(target);
        backLeft.setTargetPosition(target);
        frontLeft.setTargetPosition(target);
        frontRight.setPower(-speed);
        backRight.setPower(speed);
        backLeft.setPower(-speed);
        frontLeft.setPower(speed);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            status = "Strafing Right";
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }

    public void turnLeft(double speed, int in) {
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(target);
        backRight.setTargetPosition(target);
        backLeft.setTargetPosition(-target);
        frontLeft.setTargetPosition(-target);
        frontRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
        frontLeft.setPower(speed);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            status = "Turning Left";
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }

    public void turnRight(double speed, int in) {
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(-target);
        backRight.setTargetPosition(-target);
        backLeft.setTargetPosition(target);
        frontLeft.setTargetPosition(target);
        frontRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
        frontLeft.setPower(speed);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            status = "Turning Right";
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }

    public void reset() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        intake.setPower(0);
        turner.setPower(0);
        lift.setPower(0);
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        intake.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        turner.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void resetLift() {
        while (!liftTouch.isPressed()) {
            lift.setPower(-1);
        }
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void raiseLift(int pos) {
        int target = 0;
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        switch (pos) {
            case 1:
                target = 10;//ToDo - Lowest Goal//
                break;
            case 2:
                target = 20;//ToDO - Medium Goal//
                break;
            case 3:
                target = 30;//ToDo - High Goal//
                break;
        }
        lift.setTargetPosition(target);
        lift.setPower(1);
        while (lift.isBusy()) {
            status = "Lifting";
        }
        lift.setPower(0);
    }

    public void blueAuton() {
    }

    public void redAuton() {
    }
}