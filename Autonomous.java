/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Final Auto w/ Cam Sight", group = "Autonomous")
public class Autonomous extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    boolean canKick = false;
    boolean canHook = true;

    //The Drive Variable
    //1 is CCW? -1 is CW? maybe? huh? yeah?
    //1 is right? -1 is left?

    // Setup a variable for each drive wheel and lift motor
    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;
    double liftPower = 0;
    String set = "Null";

    //Sets variables for any motors or sensors connected
    DcMotor frontLeftDrive = null;
    DcMotor frontRightDrive = null;
    DcMotor backLeftDrive = null;
    DcMotor backRightDrive = null;
    DcMotor lift = null;
    Servo kick;
    Servo hook;
    Servo wobb;

    private static final String VUFORIA_KEY = "haha nice try :)";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        //Runs vuforia
        initVuforia();
        initTfod();

        //Make sure it's really activated
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(2.5, 16.0 / 9.0);
        }

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

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        //Reverse some motor directions (no speen this time pls)
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the robit to start
        waitForStart();
        //Resets time.elapsed
        runtime.reset();

        if (opModeIsActive()) {
            String set = "0";
            while (opModeIsActive()) {
                if(runtime.seconds() > 1.0) {
                    switch (set) {
                        case "Single":
                            //Run code for Red B, do if there's one ring
                            //Shut off tensorflow
                            tfod.shutdown();
                            //Make sure set can't trigger again (kinda useless)
                            set = "1";
                            //Debug telemet
                            telemetry.addData(">", "Run Red B");
                            telemetry.update();
                            //Run pattern for Red B (wobb 2 middle box)
                            runRedB();
                            break;
                        case "Quad":
                            //Run code for Red C, do if there's four rings
                            //Shut off tensorflow
                            tfod.shutdown();
                            //Make sure set can't trigger again (kinda useless)
                            set = "1";
                            //Debug telemet
                            telemetry.addData(">", "Run Red C");
                            telemetry.update();
                            //Run the move pattern for Red C (wobb 2 last box)
                            runRedC();
                            break;
                        default:
                            //Run code for Red A, have a timer to decide when to go
                            //Shut off tensorflow
                            tfod.shutdown();
                            //Make sure set can't trigger again (kinda useless)
                            set = "1";
                            //Debug telemet
                            telemetry.addData(">", "Run Red A");
                            telemetry.update();
                            //Run Red A movement pattern (wobb 2 first box)
                            runRedA();
                            break;
                    }
                }
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                      telemetry.addData("# Object Detected", updatedRecognitions.size());

                      // step through the list of recognitions and display boundary info.
                      int i = 0;
                      for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        set = recognition.getLabel();
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                          recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                      }
                      telemetry.update();
                    }
                }
            }
        }
        if (tfod != null) {tfod.shutdown();}
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

    public void setMovement(float drive, float turn, float strafe) {

        drive = drive*-1;

        //The math-doing part
        //Thanks mush <3 luv u
        //...Please be the same as meccanum ._.
        frontLeftPower = Range.clip(drive - turn + strafe, -1.0, 1.0);
        frontRightPower = Range.clip(-drive + turn + strafe, -1.0, 1.0);
        backLeftPower = Range.clip(-drive - turn - strafe, -1.0, 1.0);
        backRightPower = Range.clip(drive + turn - strafe, -1.0, 1.0);
        //The ol' hook em and cook em
        //Keep forgetting which one is which
        if(canHook)  {hook.setPosition(0);}
        else            {hook.setPosition(0.75);}
        if(canKick)  {kick.setPosition(0.5);}
        else            {kick.setPosition(0.9);}
        //Sets wheel motors to actually drive the robit
        //Use variables for telementary, that's why we don't just set em in the func
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
        lift.setPower(liftPower);
    }

    private void runRedA(){
        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////AUTONOMOUS CODE SECTION//////////////////////////////////

        //DRIVE, TURN (+ CW(RIGHT), - CCW(LEFT)), STRAFE (+ LEFT, - RIGHT)//

        //Hug the rings
        canHook = true;

        //Drive a tiny bit to get off the wall  `
        setMovement(0.25f, 0f, 0f);
        sleep(150);
        setMovement(0f, 0f, 0f);

        //Go straight
        setMovement(0.15f, 0f, 0f);
        sleep(5250);
        setMovement(0f, 0f, 0f);

        //Kick wobb
        wobb.setPosition(1);
        sleep(500);
        wobb.setPosition(0.45);
        sleep(500);

        //Strafe to goal
        setMovement(0f, 0f, 0.2f);
        sleep(1250);


        //Turn Back
        setMovement(0f, 0.3f, 0f);
        sleep(250);


        //Go straight
        setMovement(0.15f, 0f, 0f);
        sleep(1750);
        setMovement(0f, 0f, 0f);

        //Open hook
        canHook = false;
        setMovement(0f, 0f, 0f);
        sleep(750);

        //Lift platform
        liftPower = 0.8f;
        setMovement(0f, 0f, 0f);
        sleep(1500);

        //Close hook
        canHook = true;
        setMovement(0f, 0f, 0f);
        sleep(2100);

        //Stop liftin, go to goal
        liftPower = 0f;
        setMovement(0.15f, 0f, 0f);
        sleep(3000);

        //Plonk rings
        canKick = true;
        setMovement(0f, 0f, 0f);
        sleep(500);
        canKick = false;

        //Back away from the bucket
        setMovement(-0.3f, 0f, 0f);
        sleep(1500);
        setMovement(0f, 0f, 0f);
        sleep(99999);
    }

    private void runRedB(){
        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////AUTONOMOUS CODE SECTION//////////////////////////////////

        //DRIVE, TURN (+ CW(RIGHT), - CCW(LEFT)), STRAFE (+ LEFT, - RIGHT)//

        //Hug the rings
        canHook = true;

        //Drive a tiny bit to get off the wall  `
        setMovement(0.25f, 0f, 0f);
        sleep(150);
        setMovement(0f, 0f, 0f);

        //Go straight, boop one ring out of the way
        setMovement(0.25f, 0f, 0f);
        sleep(600);
        canHook = false;
        setMovement(0.25f, 0f, 0f);
        sleep(750);
        canHook = true;
        setMovement(0.15f, 0f, 0f);
        sleep(1000);

        //Strafe to wobb
        setMovement(0f, 0f, 0.25f);
        sleep(1250);

        //Go straight
        setMovement(0.25f, 0f, 0f);
        sleep(1250);
        setMovement(0f, 0f, 0f);

        //Kick wobb
        wobb.setPosition(1);
        sleep(500);
        wobb.setPosition(0.45);
        sleep(500);

        //Strafe away from wobble
        setMovement(0f, 0f, 0.25f);
        sleep(500);

        //Turn back to normal
        setMovement(0f, 0.2f, 0f);
        sleep(500);

        //Go straight
        setMovement(0.15f, 0f, 0f);
        sleep(2000);
        setMovement(0f, 0f, 0f);

        //Strafe back to goal
        setMovement(0f, 0f, -0.2f);
        sleep(2750);
        setMovement(0f, 0f, 0f);

        //Open hook
        canHook = false;
        setMovement(0f, 0f, 0f);
        sleep(750);

        //Lift platform
        liftPower = 0.7f;
        setMovement(0f, 0f, 0f);
        sleep(1500);

        //Close hook
        canHook = true;
        liftPower = 0f;
        setMovement(0f, 0f, 0f);

        liftPower = 0.7f;
        setMovement(0f, 0f, 0f);
        sleep(3000);

        //Stop liftin, go to goal
        liftPower = 0f;
        setMovement(0.15f, 0f, 0f);
        sleep(3000);

        //Plonk rings
        canKick = true;
        setMovement(0f, 0f, 0f);
        sleep(500);
        canKick = false;

        //Back away from the bucket
        setMovement(-0.3f, 0f, 0f);
        sleep(500);

        //Strafe to dodge wobb
        setMovement(0f, 0.1f, 0.2f);
        sleep(1500);

        //Back away from the bucket
        setMovement(-0.3f, 0f, 0f);
        sleep(1000);

        canHook = false;
        setMovement(0f, 0f, 0f);
        sleep(99999);
    }

    private void runRedC(){
        ////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////AUTONOMOUS CODE SECTION//////////////////////////////////

        //DRIVE, TURN (+ CW(RIGHT), - CCW(LEFT)), STRAFE (+ LEFT, - RIGHT)//

        //Hug the rings
        canHook = true;

        //Drive a tiny bit to get off the wall  `
        setMovement(0.25f, 0f, 0f);
        sleep(150);
        setMovement(0f, 0f, 0f);

        //Go straight, boop four rings out of the way
        setMovement(0.25f, 0f, 0f);
        sleep(500);
        canHook = false;
        setMovement(0.25f, 0f, 0f);
        sleep(1000);
        canHook = true;

        //Go straight
        setMovement(0.4f, 0f, 0f);
        sleep(1500);
        setMovement(0f, 0f, 0f);
        sleep(250);

        //Kick wobb
        wobb.setPosition(1);
        sleep(500);
        wobb.setPosition(0.45);
        sleep(500);

        //Strafe away from wobble
        setMovement(0f, 0f, 0.2f);
        sleep(1500);

        //Back up, to release hook properly
        setMovement(-0.15f, 0f, 0f);
        sleep(1000);
        setMovement(0f, 0f, 0f);

        //Open hook
        canHook = false;
        setMovement(0f, 0f, 0f);
        sleep(750);

        //Lift platform
        liftPower = 0.7f;
        setMovement(0f, 0f, 0f);
        sleep(1500);

        //Close hook
        canHook = true;
        liftPower = 0f;
        setMovement(0f, 0f, 0f);

        liftPower = 0.7f;
        setMovement(0f, 0f, 0f);
        sleep(3000);

        //Stop liftin, go to goal
        liftPower = 0f;
        setMovement(0.15f, 0f, 0f);
        sleep(3000);

        //Plonk rings
        canKick = true;
        setMovement(0f, 0f, 0f);
        sleep(500);
        canKick = false;

        //Back away from the bucket
        setMovement(-0.3f, 0f, 0f);
        sleep(1500);
        setMovement(0f, 0f, 0f);
        sleep(99999);
    }
}
