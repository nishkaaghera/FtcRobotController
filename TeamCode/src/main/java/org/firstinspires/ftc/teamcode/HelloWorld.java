package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@Disabled
@Autonomous
public class HelloWorld extends OpMode {

    @Override
    public void init(){
        telemetry.addData("Hello", "Nishka Aghera");

    }
    @Override
    public void loop () {

    }

    // Single Line Comment
    /*

   1. Hello : World, change the telemetry data to display " Hello: Your name
    2, run this code in the autonmus section instead of TelOp
     */
}
