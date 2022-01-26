package org.firstinspires.ftc.teamcode.autos.camera;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.FrenzyOpenCVDetector;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

public class FrenzyTracker {
    private OpenCvInternalCamera phoneCam;
    private FrenzyOpenCVDetector detector;
    private boolean isFlashlightOn;
    private Telemetry telemetry;

    public FrenzyTracker(HardwareMap hMap, boolean flashOn, Telemetry t) {
        isFlashlightOn = flashOn;
        telemetry = t;
        detector = new FrenzyOpenCVDetector(telemetry, 1280, 720);
        int cameraMonitorViewId = hMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hMap.appContext.getPackageName());
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

        telemetry.addData("FrenzyTracker Status:", "Initialized");
    }

    public String getPosition() {
        return detector.getPosition();
    }

    public void shutDown() {
        phoneCam.closeCameraDevice();
    }
}
