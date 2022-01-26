package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Crude TeleOp", group="TeleOp")

public class CrudeTeleOp extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotor lift = null;
    private DcMotor spinner = null;
    private DcMotor roller = null;
    private final int TICK_PER_ROTATION = 1440;
    
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Taking motors from hardware map
        frontLeft = hardwareMap.get(DcMotor.class, "front_left");
        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        backLeft = hardwareMap.get(DcMotor.class, "back_left");
        backRight = hardwareMap.get(DcMotor.class, "back_right");
        lift = hardwareMap.get(DcMotor.class, "lift");
        spinner = hardwareMap.get(DcMotor.class, "spinner");
        roller = hardwareMap.get(DcMotor.class, "roller");
        
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        roller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        // Resetting Motor Encoder
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
        
        int TETRIX_TICKS_PER_REV = 1440;
        // Initialization Complete
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        /////////////////////////////////////////
        // First Controller Code
        double speedFactor = 0.8;
        if (gamepad1.right_bumper) {
            speedFactor = 1.0;
        } else {
            speedFactor = 0.8;
        }
        double r = Math.hypot(0, -1 * gamepad1.right_stick_x);
        double robotAngle = Math.atan2(-1 * gamepad1.right_stick_x, 0) - Math.PI / 4;
        double rightX = gamepad1.left_stick_y;
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        frontLeft.setPower(v1 * speedFactor);
        frontRight.setPower(v2 * speedFactor);
        backLeft.setPower(v3 * speedFactor);
        backRight.setPower(v4 * speedFactor);

        /////////////////////////////////////////
        // Second Controller Code 
        
        // Manual Lifting
        
        // Speed is negative when left trigger is hit and positive when right trigger is hit and zero when none or both of them are hit
        double manualLiftSpeed = gamepad2.left_trigger != 0 && gamepad2.right_trigger == 0 ? 
                                    gamepad2.left_trigger * -1 : 
                                    (gamepad2.left_trigger == 0 && gamepad2.right_trigger != 0 ? 
                                        gamepad2.right_trigger : 
                                        0);  
        
        if (manualLiftSpeed != 0) {
            lift.setPower(manualLiftSpeed);
        } else {
            lift.setPower(0);
            // Automatic Lifting --- Not Implemented Yet
            // lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // lift.setTargetPosition();
        }
        if (gamepad2.dpad_up && !lift.isBusy()) {
            liftToLevel(3);
        }
        else if (gamepad2.dpad_down && !lift.isBusy()) {
            liftToLevel(0);
        }
        // Intake Rollers
        
        if (gamepad2.right_bumper && !gamepad2.left_bumper) { // Spit out
            roller.setPower(-1);
        }
        else if (gamepad2.left_bumper && !gamepad2.right_bumper) { // Take in 
            roller.setPower(0.5); // Lower speed so that it doesn't shoot it too far away
        } else {
            roller.setPower(0);
        }
        
        // Carousel Spinner
        
        if (gamepad2.y) {
            spinner.setPower(0.5);
        }
        if (gamepad2.x) {
            spinner.setPower(-0.5);
        }
        if (gamepad2.a) {
            spinner.setPower(0);
        }
        
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
    
    public void liftToLevel(int level) {
        lift.setMode(DcMotor.RunMode.RESET_ENCODERS);
        lift.setTargetPosition(1800/3 * level);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(1.0);
        telemetry.addData("LIFTING LEVEL", level);
    }

}


