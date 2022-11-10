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

  @Onverride
  public void start() {
    runtime.reset();
  }

  @Override
  public void loop() {
    liftOp();
    driveOp();
    // intakeOp(.75);
    // turnOp(1);
    hanpdleLinearSlide();
    handleClaw();
    telemetry.addData("Status", "Run Time: " + runtime);
    telemetry.update();
  }

  // function to do turning and movment, supports mekanum wheels
  public void driveOp() {
    // set stick to 0 if it is below smal value, otherwise invert (left stick x)
    if (Math.abs(gamepad1.left_stick_x) < 0.05) {
      deadZoneX = 0;
    } else {
      deadZoneX = -gamepad1.left_stick_x;
    }

    // same as previous, but left stick y
    if (Math.abs(gamepad1.left_stick_y) < 0.05) {
      deadZoneY = 0;
    } else {
      deadZoneY = -gamepad1.left_stick_y;
    }

    // same as previous, but right stick x and don't invert
    if (Math.abs(gamepad1.right_stick_x) < 0.05) {
      deadZoneRotate = 0;
    } else {
      deadZoneRotate = gamepad1.right_stick_x;
    }

    // bunch of math for the mekanum wheels, i don't really understand the math
    double r = Math.hypot(deadZoneX, -deadZoneY);
    double robotAngle = Math.atan2(-deadZoneY, deadZoneX) - Math.PI / 4;
    double rightX = deadZoneRotate / 1.25;
    final double v1 = r * Math.cos(robotAngle) + rightX;
    final double v2 = r * Math.sin(robotAngle) - rightX;
    final double v3 = r * Math.sin(robotAngle) + rightX;
    final double v4 = r * Math.cos(robotAngle) - rightX;

    //set final motor powers
    frontRight.setPower(v1 * driveSpeed);
    frontLeft.setPower(v4 * driveSpeed);
    backRight.setPower(v3 * driveSpeed);
    backLeft.setPower(v2 * driveSpeed);
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
    double deadZoneA;
    double deadZoneY;

    // get input from gamepad
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
    // sum = using a and y kind of like 1 joystick
    final double v1 = deadZoneA + deadZoneY;
    lift.setPower(v1 * liftSpeed);
    lift2.setPower(v1 * liftSpeed);
  }
}
