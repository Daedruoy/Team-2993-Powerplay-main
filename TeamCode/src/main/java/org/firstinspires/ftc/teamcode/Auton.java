package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Blue Autonomous")
public class Auton extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    public static final int cpr = 1680; //Counts per Revolution//
    public static final double cpi = cpr / (4 * Math.PI); //Counts per Inch//
    private DcMotorEx frontRight = null, backRight = null, backLeft = null, frontLeft = null;
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        frontRight = hardwareMap.get(DcMotorEx.class, "MotorC0");
        frontRight.setDirection(DcMotorEx.Direction.FORWARD);
        backRight = hardwareMap.get(DcMotorEx.class, "MotorC1");
        backRight.setDirection(DcMotorEx.Direction.FORWARD);
        backLeft = hardwareMap.get(DcMotorEx.class, "MotorC2");
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        frontLeft = hardwareMap.get(DcMotorEx.class, "MotorC3");
        frontLeft.setDirection(DcMotorEx.Direction.REVERSE);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        goForward(.5,48);
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Status:", "Running the Tele-Operation Functions");
        telemetry.update();
    }
    public void goForward(double speed, int in) {
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        int target = (int) (in * cpi);
        frontRight.setTargetPosition(target);
        backRight.setTargetPosition(target);
        backLeft.setTargetPosition(target);
        frontLeft.setTargetPosition(target);
        frontRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);
        frontLeft.setPower(speed);
        frontRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        while (frontRight.isBusy() && backRight.isBusy() && frontLeft.isBusy() && backLeft.isBusy()) {
            telemetry.addData("Status:", "Forwards");
        }
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }
}