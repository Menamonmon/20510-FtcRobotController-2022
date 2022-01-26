package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDController {
    /**
     * construct PID controller
     * @param Kp Proportional coefficient
     * @param Ki Integral coefficient
     * @param Kd Derivative coefficient
     */
    private double Kp, Ki, Kd, lastError, integralSum;
    private ElapsedTime timer;
    public PIDController(double Kp, double Ki, double Kd) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        timer = new ElapsedTime();
        lastError = 0;
        integralSum = 0;
    }

    /**
     * update the PID controller output
     * @param target where we would like to be, also called the reference
     * @param state where we currently are, I.E. motor position
     * @return the command to our motor, I.E. motor power
     */
    public double update(double target, double state) {
        // PID logic and then return the output
        // obtain the encoder position
        // calculate the error
        timer.reset();
        double error = target - state;

        // rate of change of the error
        double derivative = (error - lastError) / timer.seconds();

        // sum of all error over time
        integralSum = integralSum + (error * timer.seconds());

        double out = (Kp * error) + (Ki * integralSum) + (Kd * derivative);

        lastError = error;

        // reset the timer for next time
        timer.reset();
        return out;
    }

    double getLastError() { return lastError; }
    double getIntegralSum() { return integralSum; }
}
