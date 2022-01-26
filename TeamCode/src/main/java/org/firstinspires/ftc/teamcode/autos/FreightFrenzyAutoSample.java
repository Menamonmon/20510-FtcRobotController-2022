package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.autos.camera.FrenzyTracker;
import org.firstinspires.ftc.teamcode.autos.controls.AutoArmController;
import org.firstinspires.ftc.teamcode.autos.controls.AutoTurretController;

@Autonomous(name="Freight Frenzy Auto Sample", group="Autos")
public class FreightFrenzyAutoSample extends LinearOpMode {
    DcMotorEx armMotor;
    DcMotorEx turretMotor;

    // Auto Turret and Arm Controls
    AutoArmController arm;
    AutoTurretController turret;

    // Team Shipping Element Detector
    FrenzyTracker tracker;

    String tsePosition;

    @Override
    public void runOpMode() {
        armMotor = hardwareMap.get(DcMotorEx.class, "arm");
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");

        AutoArmController arm = new AutoArmController(armMotor, telemetry);
        AutoTurretController turret = new AutoTurretController(turretMotor, telemetry);

        tracker = new FrenzyTracker(hardwareMap, false, telemetry);

        waitForStart();

        tsePosition = tracker.getPosition();
        tracker.shutDown();

        // Main loop
        while (opModeIsActive()) {
            // Keep the arm centered
            turret.resetArm();

            telemetry.addData("Team Shipping Element Position", tsePosition);

            telemetry.update();
        }
    }
}
