package org.firstinspires.ftc.teamcode.autos.controls;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.PIDController;

public class AutoTurretController {
    // Need to be changed to the right positions with experimentation
    private final double origin = 0, left = 0, right = 0;

    private DcMotorEx turretMotor;
    private PIDController controller;
    private Telemetry telemetry;
    public AutoTurretController(DcMotorEx motor, Telemetry t) {
        turretMotor = motor;
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        controller = new PIDController(0, 0, 0); ///// NEED TO BE TUNED
        telemetry = t;
    }

    private void goToPosition(double pos) {
        double command = controller.update(pos, turretMotor.getCurrentPosition());
        turretMotor.setPower(command);
    }

    public void turnRight() {
        goToPosition(right);
    }

    public void turnLeft() {
        goToPosition(left);
    }

    public void resetArm() {
        goToPosition(origin);
    }
}
