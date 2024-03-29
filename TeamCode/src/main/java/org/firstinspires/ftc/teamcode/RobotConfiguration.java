package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;

import static java.lang.Thread.sleep;
import static org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot.MID_SERVO;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific hardware for the Ultimate Goal Season.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are camel case with no spaces.
 *
 * Motor channel:  Left Front drive motor:          "leftFrontDrive"
 * Motor channel:  Right Front drive motor:         "rightFrontDrive"
 * Motor channel:  Left Back Front drive motor:     "leftBackDrive"
 * Motor channel:  Right Back drive motor:          "rightBackDrive"
 * Motor channel:  Shooter drive motor:             "Shooter"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class RobotConfiguration {

    /* Private OpMode members. */ //May make public later
     DcMotor leftFrontDrive = null;
     DcMotor rightFrontDrive = null;
     DcMotor leftBackDrive = null;
     DcMotor rightBackDrive = null;
     DcMotor shooter = null;
     CRServo intakeTwo = null;
     CRServo intakeWheels = null;
     Servo intakeThree = null;

    // Declare Contants  (not variable, can't change in program)
    private final double THRESHOLD = 0.05;
    private final double UPPOS = 0.5; //Maybe Should change to a zero position
    private final double DOWNPOS = 0;
    private final double STOPCRSERVO = 0.5;
    private final double FORWARDCRSERVO = 1.0; //test direction
    private final double REVERSECRSERVO = 0;
    private final double GOALDISTANCE = 1; //Change during testing also add a unit

    /* local OpMode members. */
    HardwareMap hwMap = null;
    private final ElapsedTime period = new ElapsedTime();
    //VoltageSensor vs = hwMap.voltageSensor.get("DQ16N6NX"); //Change this

    private double driveYaw = 0;   // Positive is CW


    /* Constructor */
    public RobotConfiguration() {

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        leftFrontDrive = hwMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive = hwMap.get(DcMotor.class, "rightFrontDrive");
        leftBackDrive = hwMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive = hwMap.get(DcMotor.class, "rightBackDrive");
        shooter = hwMap.get(DcMotor.class, "shooter");


        //Temporary directions for drive train, change after testing if needed.
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);


        //Intake Hardware
        intakeTwo = hwMap.crservo.get("intakeTwo");
        intakeTwo.resetDeviceConfigurationForOpMode();
        intakeWheels = hwMap.crservo.get("intakeWheels");
        intakeWheels.resetDeviceConfigurationForOpMode();
        //intakeWheels.setPower(STOPCRSERVO);
        intakeThree = hwMap.servo.get("intakeThree");


        shooter.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set all motors to zero power
        stopDriveTrain();
        stopServo();


        // Set all motors to run without encoders. To be removed later i am testing something
        // Change to RUN_USING_ENCODERS if encoders are installed.
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


    }

    /**
     * Uses x and y vectors to determine and set the motor power.
     *
     * @param xVector The amount to move the robot left or right
     * @param yVector The amount to move the robot forward or backward
     *                Precondition: Both the parameters must fall between -1.0 - 1.0
     */
   public void drive(float xVector, float yVector, float leftTrigger, float rightTrigger) {
        double forward = yVector;
        double strafe = xVector;
        double twist = -leftTrigger + rightTrigger;

        double fl = forward + twist - strafe;
        double fr = forward - twist - strafe;
        double bl = forward + twist + strafe;
        double br = forward - twist + strafe;

        double max = Math.max(Math.abs(fl), Math.max(Math.abs(bl), Math.max(Math.abs(br), Math.abs(fr))));
        if (max > 1) {
            fl /= max;
            fr /= max;
            br /= max;
            bl /= max;
        }

        leftFrontDrive.setPower(2 * fl);
        leftBackDrive.setPower(2 * bl);
        rightFrontDrive.setPower(2 * fr);
        rightBackDrive.setPower(2 * br);
    }


    public void setShooter(boolean a) {
        if (a) {
            shooter.setPower(-1);
        }
        else{
            shooter.setPower(0);
        }
    }

    public void shootingBasedOnDistance(double velocity) {

    }

    /**
     * Shoots for the high goal
     */
/*
    public void driveTwo(double m, double n, double o){
        if (m > .05 && n > .05){
            leftFrontDrive.setPower(m);
            leftBackDrive.setPower(m);
            rightFrontDrive.setPower(n);
            rightBackDrive.setPower(n);
        }
        if (){
            leftFrontDrive.setPower(-m);
            leftBackDrive.setPower(-m);
            rightFrontDrive.setPower(-n);
            rightBackDrive.setPower(-n);
        }
        else if (o > .05){
            leftFrontDrive.setPower(-o);
            leftBackDrive.setPower(-o);
            rightFrontDrive.setPower(o);
            rightBackDrive.setPower(o);
        }
        else if (o < -.05){
            leftFrontDrive.setPower(o);
            leftBackDrive.setPower(o);
            rightFrontDrive.setPower(-o);
            rightBackDrive.setPower(-o);
        }
        else{
            this.stopDriveTrain();
        }
    }

/*
    /**
     * Will run the shooter if true
     */
    public void stopServo() {
        intakeWheels.setPower(0);
        intakeTwo.setPower(0);
    }
    public void drivetwo(double b){
        if (b>0) {
            leftFrontDrive.setPower(1);
            leftBackDrive.setPower(1);
            rightFrontDrive.setPower(1);
            rightBackDrive.setPower(1);
        }
        if (b<0) {
            leftFrontDrive.setPower(-1);
            leftBackDrive.setPower(-1);
            rightFrontDrive.setPower(-1);
            rightBackDrive.setPower(-1);
        }
        else{
            leftFrontDrive.setPower(0);
            leftBackDrive.setPower(0);
            rightFrontDrive.setPower(0);
            rightBackDrive.setPower(0);
        }
    }
    public void stopDriveTrain() {
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void intakeWheels(boolean x, boolean z) {
        if (x) {
            intakeWheels.setPower(-1);
        }
        else if(z){
          intakeWheels.setPower(1);
      }
        else {
            intakeWheels.setPower(.5);

        }
    }

    public void intakeTwo(boolean j, boolean k) {
        if (j) {
            intakeTwo.setPower(-1);
        }
        else if(k){
            intakeTwo.setPower(1);
        }
        else {
            intakeTwo.setPower(0);

        }
    }

    public void intakeThree(boolean i) {
        if (i) {
            intakeThree.setPosition(0);
        }

        else {
            intakeThree.setPosition(1);

        }
    }
        /**
         * Returns the current voltage of the robot's main battery
         */
        //public double getBatteryPower(){
        //return vs.getVoltage();
        //}

    /*public double getDistance(){
        return vuforia.distanceFromTarget();
    }*/

    }



