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
  private DcMotorEx frontRight = null, backRight = null, backLeft = null, frontLeft = null, lift = null, lift2 = null;
  private CRServo slideServo = null;
  private Servo clawServo = null;
  private boolean clawClosed = false;

  // config variables, adjust these to change various things about robot
  private double slideServoSpeed = 1.0;
  private double clawClosedPos = 1.0;
  private double clawOpenPos = 0.0;
  private double driveSpeed = 0.85;
  private double liftSpeed = 0.5;

  /*
    Controls:
    left stick -- moves bot in direction of stick using mechanums
    right stick -- turns bot, only x value does anything
    triggers -- moves lift up and down
    bumpers -- moves linear slide motor up and down
    x -- toggles claw open / closed
  */
  
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
    liftOp();
    driveOp();
    // intakeOp(.75);
    // turnOp(1);
    handleLinearSlide();
    handleClaw();
    telemetry.addData("Status", "Run Time: " + runtime);
    telemetry.update();
  }

  // function to do turning and movment, supports mekanum wheels
  public void driveOp() {
    double deadZoneDriveX = .5, deadZoneDriveY = .5, deadZoneDriveRotate = .5;
    if (Math.abs( gamepad1.left_stick_x) < 0.05) {
      deadZoneDriveX = 0;
    } else {
      deadZoneDriveX = -gamepad1.left_stick_x;
    }
    if (Math.abs(gamepad1.left_stick_y) < 0.05) {
      deadZoneDriveY = 0;
    } else {
      deadZoneDriveY = gamepad1.left_stick_y;
    }
    if (Math.abs(gamepad1.right_stick_x) < 0.05) {
      deadZoneDriveRotate = 0;
    } else {
      deadZoneDriveRotate = gamepad1.right_stick_x;
    }
    double r = Math.hypot(deadZoneDriveX, -deadZoneDriveY);
    double robotAngle = Math.atan2(-deadZoneDriveY, deadZoneDriveX) - Math.PI / 4;
    double rightX = deadZoneDriveRotate / 1.25;
    final double v1 = r * Math.cos(robotAngle) + rightX;
    final double v2 = r * Math.sin(robotAngle) - rightX;
    final double v3 = r * Math.sin(robotAngle) + rightX;
    final double v4 = r * Math.cos(robotAngle) - rightX;
    frontRight.setPower(v1 * driveSpeed);
    frontLeft.setPower(v2 * driveSpeed);
    backRight.setPower(v3 * driveSpeed);
      backLeft.setPower(v4 * driveSpeed);
  }



  // function to handle linear slide
  public void handleLinearSlide ()
  {
    //get inputs from bumpers
    double power = 0;
    power += gamepad1.left_bumper ? -1 : 0;
    power += gamepad1.right_bumper ? 1 : 0;

    slideServo.setPower(power * slideServoSpeed); // set servo power
  }

  // function to toggle claw between closed and open based on when driver presses the a button
  public void handleClaw ()
  {
    if (gamepad1.x) clawClosed = !clawClosed;

    if (clawClosed) clawServo.setPosition(clawClosedPos);
    else clawServo.setPosition(clawOpenPos);
  }
  
  // function to handle 4 bar
  public void liftOp() {
    // get input from gamepad
    double deadZoneTriggers = gamepad1.right_trigger - gamepad1.left_trigger;
    
    if (deadZoneTriggers < 0.05) deadZoneTriggers = 0;

    lift.setPower(deadZoneTriggers * liftSpeed);
    lift2.setPower(deadZoneTriggers * liftSpeed);
  }
}
