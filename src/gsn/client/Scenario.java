package gsn.client;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Scenario {
    static Scenario defaultScenario() {
        return new Scenario(1, 100, 3);
    }

    int numberMessage;
    int delayMillis;
    int lifeTimeSecond;

    public Scenario(int numberMessage, int delayMillis, int lifeTimeSecond) {
        this.numberMessage = numberMessage;
        this.delayMillis = delayMillis;
        this.lifeTimeSecond = lifeTimeSecond;
    }
}
