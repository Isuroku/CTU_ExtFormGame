package ekimoiva;

public class CStrategiesUtil
{
    String _agent;
    String _bandit;
    float _utility;

    CStrategiesUtil(String agent_strategy, String bandit_strategy, float utility)
    {
        _agent = agent_strategy;
        _bandit = bandit_strategy;
        _utility = utility;
    }

    public String GetAgentStrategy() { return _agent; }
    public String GetBanditStrategy() { return _bandit; }
    public float GetUtility() { return _utility; }

    @Override
    public String toString()
    {
        return String.format("%s %s %f", _agent, _bandit, _utility);
    }
}
