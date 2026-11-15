package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
/*------------------------------------------------------------------------------------------
 * This class is the FULL auto-alignment class. It includes systems to check what id we are
 * facing and auto align to it. The id can vary from 30 - 45 (the current BioBuzz id's).
 * We will have to adjust the kP, kD, and stepSizes to work with our camera and bot.
 * ------------------------------------------------------------------------------------------*/
@TeleOp
public class AprilTagAutoAlignmentMecanumDrivetrain extends OpMode {

    private final AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    private final MecanumDrive drive = new MecanumDrive();

    //------------------------- PD Controller -------------------------

    double kP = 0.002;
    double error = 0;
    double lastError = 0;
    double goalX = 0; // Offset Here
    double angleTolerance = 0.2;
    double kD = 0.0001;
    double curTime = 0;
    double lastTime = 0;


    //------------------------- Driving Setup -------------------------
    double forward, strafe, rotate;

    //------------------------- Controller Based PD Tuning -------------------------


    double[] stepSizes = {1.0, 0.1, 0.001,0.0001};
    int stepIndex = 2;


    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        drive.init(hardwareMap, false);

        telemetry.addLine("Initialized");
    }


    public void start() {
        resetRuntime();
        curTime = getRuntime();
    }


    @Override
    public void loop() {
        //------------------------- Get Mecanum Drive Inputs -------------------------

        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        //------------------------- Get April Tag Info -------------------------

        aprilTagWebcam.update();
        AprilTagDetection targetTag = aprilTagWebcam.getTagInRange(30, 45);

        //------------------------- Auto Allign Rotation Logic -------------------------

        if (gamepad1.left_trigger > 0.3) {
            if (targetTag != null) {
                error = goalX - targetTag.ftcPose.bearing; // tx
                if (Math.abs(error) < angleTolerance) {
                    rotate = 0;
                } else {
                    double pTerm = error * kP;

                    curTime = getRuntime();
                    double dT = curTime - lastTime;
                    double dTerm = ((error - lastError) / dT)* kD;

                    rotate = Range.clip(pTerm + dTerm, -0.4, 0.4);

                    lastError = error;
                    lastError = curTime;
                }
            } else {
                lastTime = getRuntime();
                lastError = 0;

            }
        } else {
            lastError = 0;
            lastTime = getRuntime();

        }

        //------------------------- Drive Motors! -------------------------

        drive.drive(forward, strafe, rotate);

        //------------------------- Update P And D On The Fly -------------------------
        // 'B' button cycles through the different step sizes for tuning precision
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length; // Modulo wraps the index back to 0.
        }

        // D-pad left/right adjusts the P gain.
        if (gamepad1.dpadLeftWasPressed()) {
            kP -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()) {
            kP += stepSizes[stepIndex];
        }

        // D-pad up/down adjusts the D gain.
        if (gamepad1.dpadUpWasPressed()) {
            kD += stepSizes[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()) {
            kD -= stepSizes[stepIndex];
        }

        // ------------ Telemetry -------------
        if (targetTag != null) {
            if (gamepad1.left_trigger > 0.3) {
                telemetry.addLine("AUTO ALIGN");
            }
            aprilTagWebcam.displayDetectionTelemetry(targetTag);
            telemetry.addData("Error", error);
        } else {
            telemetry.addLine("MANUAL Rotate Mode");
        }
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P", "%.4f (D-Pad L/R)", kP);
        telemetry.addData("Tuning D", "%.4f (D-Pad U/D)", kD);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);

    }
}
