package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous
public class AprilTagWebcamExample extends OpMode {
    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();

    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        // Update the vision portal to get the latest camera frame
        aprilTagWebcam.update();

        /*------------------------------------------------------------------------------------------
        Look for any tag with an ID between 30 and 45
        * Adding variability in the April Tags we detect. We can detect any of the
        * 16 April tags in the Biobuzz season. Telemetry has now also been edited
        * to explain values showing up.
        ------------------------------------------------------------------------------------------
        * Helpful Tip: If watching a tutorial, say by Brogan M. Pratt, and he tells you to type
        * "id20" somewhere, replace it with "targetTag", as this lets us see all 16 possible values.
        ------------------------------------------------------------------------------------------ */
        AprilTagDetection targetTag = aprilTagWebcam.getTagInRange(30, 45);

        // Display telemetry for the found tag (if any)
        aprilTagWebcam.displayDetectionTelemetry(targetTag);

        if (targetTag != null) {
            double bearing = targetTag.ftcPose.bearing;
            double range = targetTag.ftcPose.range;


            telemetry.addData("Target ID Found", targetTag.id);
        } else {
            telemetry.addData("Status", "Looking for IDs 30-45...");
        }

        telemetry.update();
    }
}
