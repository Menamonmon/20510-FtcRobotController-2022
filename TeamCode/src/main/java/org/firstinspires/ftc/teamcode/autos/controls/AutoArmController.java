package org.firstinspires.ftc.teamcode.autos.controls;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.PIDController;

public class AutoArmController {
    // Need to be changed to the right positions with experimentation
    private final double firstLevelPosition = 0, secondLevelPosition = 0, thirdLevelPosition = 0;

    private DcMotorEx armMotor;
    private PIDController controller;
    private Telemetry telemetry;
    public AutoArmController(DcMotorEx motor, Telemetry t) {
        armMotor = motor;
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        controller = new PIDController(0, 0, 0); ///// NEED TO BE TUNED
        telemetry = t;
    }

    private void goToPosition(double pos) {
        double command = controller.update(pos, armMotor.getCurrentPosition());
        armMotor.setPower(command);
    }

    /*
    * @param level: 1 - 3 value where 1 is the bottom and 3 is the top
    * */
    public void goToLevel(int level) {
        if (level == 1) {
            goToPosition(firstLevelPosition);
        } else if (level == 2) {
            goToPosition(secondLevelPosition);
        } else if (level == 3) {
            goToPosition(thirdLevelPosition);
        } else {
            telemetry.addData("ERROR", "INVALID LEVEL VALUE FOR ARM CONTROLLER AUTO");
        }
    }

    public void resetArm() {
        goToPosition(0);
    }
}
