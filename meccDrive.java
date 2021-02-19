//Uhh pls work
//Unloads packages, have TeleOp and Autonomous mode code to run for SkyStone (it's old I know)
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

@TeleOp(name="meccDrive", group="Linear Opmode")
//@Disabled
public class meccDrive extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    //The entire program (while loop is inside)
    public void runOpMode() {
        //Sets variables for any motors or sensors connected
        //Sets all as null, cause variable scope is fun and I can't just instantly loc map em (:
        //Took me a whole day to fix
        DcMotor frontLeftDrive = null;
        DcMotor frontRightDrive = null;
        DcMotor backLeftDrive = null;
        DcMotor backRightDrive = null;
        DcMotor lift = null;
        Servo kick;
        Servo hook;
        Servo wobb;

        // Setup a variable for each drive wheel and lift motor
        // Used for telemetry at the end, so we can debug
        double frontLeftPower;
        double frontRightPower;
        double backLeftPower;
        double backRightPower;
        double liftPower;


        //Toggle button
        boolean canHook = false;

        //Set motor variables on the robit
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "MotorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "MotorFR");
        backLeftDrive  = hardwareMap.get(DcMotor.class, "MotorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "MotorBR");
        lift = hardwareMap.get(DcMotor.class, "Lift");


        //Set servo variables on the bitro
        kick = hardwareMap.get(Servo.class, "Kick");
        hook = hardwareMap.get(Servo.class, "Hook");
        wobb = hardwareMap.get(Servo.class, "Wobb");


        //Reverse some motor directions (no speen this time pls)
        //Reverse the right side motors
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        lift.setDirection(DcMotor.Direction.FORWARD);

        // Add telemet and wait for the robit to start
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        //Resets time.elapsed   //TODO (not really) make it min:sec?    //Use mod/div func by 60
        runtime.reset();

        ////MAIN LOOP, WHAT RUNS EVERY 'FRAME'////
        while (opModeIsActive()) {

            //Hoo boy the math is complicated.      //Starts off with a multiplier of 0.75, to make it weaker
            //Reduces that multiplier if the sprint button is pulled    //Mults by stick value, to get the final power
            //Honestly don't know how it works, but it do, so yeah
            ////LY (Forward/Backwards)
            double drive = (-0.75+((1-gamepad1.right_trigger)*0.5))*gamepad1.left_stick_y;
            ////LX (Strafing Left/Right)
            double strafe = (-0.75+((1-gamepad1.right_trigger)*0.5))*gamepad1.left_stick_x;
            ////RX (Turning Left/Right)
            double turn = (-0.75+((1-gamepad1.right_trigger)*0.25))*-gamepad1.right_stick_x;


            //The math-doing part
            //Thanks mr.mush <3 luv u
            // Uses range to keep it from being too stronk   //Even after all the math that makes it less stronk
            //Also for some reason, up is -1? so some drives are reversed, but it works
            //...Please be the same as meccanum ._.     //It is ^w^ (almost)
            frontLeftPower = Range.clip((-drive - turn + strafe)*1.2, -0.75, 0.75);
            frontRightPower = Range.clip((drive + turn + strafe)*1.2, -0.75, 0.75);
            backLeftPower = Range.clip((drive - turn - strafe)*0.8, -0.75, 0.75);
            backRightPower = Range.clip((-drive + turn - strafe)*0.8, -0.75, 0.75);

            //Elevator function
            //Used to go wayy to slow
            //Still too slow to kickflip    //Just SLAM it into the ground
            //Keeps falling off rack    //Not anymore, haha     //Watch out for brass markers   //Put in stopper?
            //Left trigger is sprint 4 lift, makes it go twice as fast.
            if(gamepad1.dpad_up)        {liftPower = 0.4+(0.4*gamepad1.left_trigger);}
            else if(gamepad1.dpad_down) {liftPower = -0.4+(-0.4*gamepad1.left_trigger);}
            else                        {liftPower = 0;}


            //The ol' hook em and cook em       //Now with wobble kick! ^w^     //Which we'll never use in comp ._ .
            //Keep forgetting which one is which    //Apparently everyone does
            //TODO test toggle mode?     //Toggle for toggle mode?       //Like PD hold triggers?
            //Toggle mode (which brandon doesn't want now)

            /*if          (gamepad1.a && !canHook)           {hook.setPosition(0.1); sleep(100); canHook = true;}         // A IS HOOK
              else if     (gamepad1.a && canHook)            {hook.setPosition(0.75); sleep(100); canHook = false;} */

            //Fine it's hold now :(
            if(gamepad1.a)   {hook.setPosition(0.1);}
            else             {hook.setPosition(0.75);}
            if(gamepad1.b)   {kick.setPosition(0.5);}       // B IS KICK
            else             {kick.setPosition(0.89);}
            if(gamepad1.x)   {wobb.setPosition(1);}          // X IS WOBB KICK (more of a test than anything)
            else             {wobb.setPosition(0.4);}

            //Sets wheel motors to actually drive the robit
            //Use variables for telemetry, that's why we don't just set em in the func
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);
            lift.setPower(liftPower);

            //// Show the elapsed game time and wheel power.
            //Run time
            telemetry.addData("Status", "Run Time: " + runtime);
            //Wheel Power
            telemetry.addData("FL - FR", "FL (%.2f), FR (%.2f)", frontLeftPower, frontRightPower);
            telemetry.addData("BL - BR", "BL (%.2f), BR (%.2f)", backLeftPower, backRightPower);
            telemetry.addData("LIFT", "LIFT (%.2f)", liftPower);
            telemetry.addData("RT", "RT (%.2f)", gamepad1.right_trigger);
            //Updates display on driver phone
            telemetry.update();

            // ^-^
            // -Tyson
        }
    }
}
