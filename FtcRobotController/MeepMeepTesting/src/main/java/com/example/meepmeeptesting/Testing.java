package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;

public class Testing {
    public static void main(String args[]) {
        MeepMeep mm = new MeepMeep(600)
                // Set field image
                .setBackground(MeepMeep.Background.FIELD_FREIGHT_FRENZY)
                // Set theme
                .setTheme(new ColorSchemeRedDark())
                // Background opacity from 0-1
                .setBackgroundAlpha(1f)
                // Set constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -64,Math.toRadians(90)))
                                //.splineTo(new Vector2d(-11.0, -40.0), 90)
                                .splineTo(new Vector2d(-11, -45), Math.toRadians(90))
                                .waitSeconds(3)
                                .turn(Math.toRadians(-90))
                                .lineTo(new Vector2d(-47, -47))
                                .splineTo(new Vector2d(-60, -60), Math.toRadians(270))
                                .waitSeconds(3)
                                .splineTo(new Vector2d(-47, -43.5), Math.toRadians(0))
                                .lineTo(new Vector2d(38, -43.5))
                                .build()
                )
                .start();

    }
}