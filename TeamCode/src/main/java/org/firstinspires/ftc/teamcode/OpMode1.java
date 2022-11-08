package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "Test Op Mode (Working2)")
public class OpMode1 extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    double deadZoneSX;
    double deadZoneSY;
    private DcMotorEx frontRight = null, backRight = null, backLeft = null, frontLeft = null, lift = null, lift2 = null,/* add back intake = null,*/ turn = null;
    private CRServo slideServo = null;
    private Servo clawServo = null;
    private boolean clawClosed = false;
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        frontRight = hardwareMap.get(DcMotorEx.class, "MotorC0");
        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight = hardwareMap.get(DcMotorEx.class, "MotorC1");
        backRight.setDirection(DcMotorEx.Direction.REVERSE);
        backLeft = hardwareMap.get(DcMotorEx.class, "MotorC2");
        backLeft.setDirection(DcMotorEx.Direction.FORWARD);
        frontLeft = hardwareMap.get(DcMotorEx.class, "MotorC3");
        frontLeft.setDirection(DcMotorEx.Direction.FORWARD);
        lift = hardwareMap.get(DcMotorEx.class, "MotorE0"); // only one that runs
        lift.setDirection(DcMotorEx.Direction.REVERSE);
        //intake = hardwareMap.get(DcMotorEx.class, "MotorE1"); need servos
        //intake.setDirection(DcMotorEx.Direction.FORWARD);
        lift2 = hardwareMap.get(DcMotorEx.class, "MotorE1");
        lift2.setDirection(DcMotorEx.Direction.FORWARD);
        turn = hardwareMap.get(DcMotorEx.class, "MotorE2");
        turn.setDirection(DcMotorEx.Direction.FORWARD);

       // slideServo = hardwareMap.crservo.get(Servo.class, "ServoLinearSlide"); // Linear slide servo
        slideServo = hardwareMap.crservo.get("ServoLinearSlide");
        slideServo.setDirection(CRServo.Direction.FORWARD);

        // idk if this needs to be a continuous rotation servo or not, we aren't using it as one
        // but is the actual physical servo technically a 360 servo?
        clawServo = hardwareMap.get(Servo.class, "ServoClaw");
        clawServo.setDirection(Servo.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        runtime.reset();
    }

    @Override
    public void loop() {
        liftOp(.5);
        driveOp(.85);
       // intakeOp(.75);
        turnOp(1);
        handleLinearSlide();
        handleClaw();
        telemetry.addData("Status", "Run Time: " + runtime);
        telemetry.update();
    }

    public void driveOp(double driveSpeed) {
        if (Math.abs(gamepad1.right_stick_x) > .05) {
            deadZoneSY = -gamepad1.right_stick_x;
        } else if (Math.abs(gamepad1.left_stick_y) > .05) {
            deadZoneSX = gamepad1.left_stick_y;
        } else {
            deadZoneSX = 0;
            deadZoneSY = 0;
        }
        final double left = deadZoneSX + deadZoneSY;
        final double right = deadZoneSX - deadZoneSY;
        frontLeft.setPower(left * driveSpeed);
        backLeft.setPower(left * driveSpeed);
        frontRight.setPower(right * driveSpeed);
        backRight.setPower(right * driveSpeed);
    }

  /*  public void intakeOp(double speed) {
        double deadZoneRT = 0;
        double deadZoneLT = 0;
        if (gamepad1.right_trigger > .05) {
            deadZoneRT = gamepad1.right_trigger;
            deadZoneLT = 0;
        } else if (gamepad1.left_trigger > .05) {
            deadZoneLT = -gamepad1.left_trigger;
            deadZoneRT = 0;
        }
        final double v1 = deadZoneLT + deadZoneRT;
        intake.setPower(v1 * speed); add back for servos
    }*/

    public void handleLinearSlide ()
    {
        double power = 0;
        power += gamepad1.left_bumper ? -1 : 0;
        power += gamepad1.right_bumper ? 1 : 0;

        slideServo.setPower(power * 1);
    }

    // function to toggle claw between closed and open based on when driver presses the a button
    public void handleClaw ()
    {
        if (gamepad1.x) clawClosed = !clawClosed;

        if (clawClosed) clawServo.setPosition(1);
        else clawServo.setPosition(0);
    }

    public void turnOp(double speed) {
        double deadZoneA;
        double deadZoneX;
        if (gamepad1.b) {
            deadZoneA = 1;
            deadZoneX = 0;
        } else if (gamepad1.x) {
            deadZoneX = -1;
            deadZoneA = 0;
        } else {
            deadZoneA = 0;
            deadZoneX = 0;
        }
        final double v1 = deadZoneA + deadZoneX;
        turn.setPower(v1 * speed);
    }

    public void liftOp(double speed) {
        double deadZoneA;
        double deadZoneY;
        if (gamepad1.a) {
            deadZoneA = 1;
            deadZoneY = 0;
        } else if (gamepad1.y) {
            deadZoneY = -1;
            deadZoneA = 0;
        } else{
            deadZoneA = 0;
            deadZoneY = 0;
        }
        final double v1 = deadZoneA + deadZoneY;
        lift.setPower(v1 * speed);
        lift2.setPower(v1 * speed);
    }
}