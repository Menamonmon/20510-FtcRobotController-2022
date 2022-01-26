package org.firstinspires.ftc.teamcode.autos;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.utils.FrenzyOpenCVDetector;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

@Autonomous(name = "Testing Camera Detection", group = "Auto Testing")
public class CameraVisionTracking extends LinearOpMode {
    OpenCvInternalCamera phoneCam;
    String position;
    FrenzyOpenCVDetector detector = new FrenzyOpenCVDetector(telemetry, 1280, 720);
    private boolean isFlashlightOn = false;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                phoneCam.setFlashlightEnabled(isFlashlightOn);
                phoneCam.setPipeline(detector);
                phoneCam.startStreaming(1280, 720, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }

            @Override
            public void onError(int errorCode)
            {
                telemetry.addData("ERROR", "Can not run camera listener");
            }
        });

        telemetry.update();
        waitForStart();
        position = detector.getPosition();
        phoneCam.closeCameraDevice();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                telemetry.addData("ELEMENT POSITION", position);
                telemetry.update();
            }
        }
    }

}
