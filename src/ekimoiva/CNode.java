package ekimoiva;

public class CNode
{
    private Vector2D _pos;
    private CNode _parent;
    private int _bandit_count;
    private int _danger_count;
    private float _probability;
    private String _agent_strategy;
    private String _bandit_strategy;
    private float _utility;

    CNode(Vector2D pos, CNode parent, int bandit_count, int danger_count, float probability, float utility, char agent_step, char bandit_step)
    {
        _pos = pos;
        _bandit_count = bandit_count;
        _danger_count = danger_count;
        if(parent != null)
        {
            _parent = parent;

            _probability = probability * parent.GetProbability();
            _utility = parent.GetUtility() + utility;

            _agent_strategy = parent.GetAgentStrategy();
            if(agent_step != 0)
                _agent_strategy += agent_step;

            _bandit_strategy = parent.GetBanditStrategy();
            if(bandit_step != 0)
                _bandit_strategy += bandit_step;
        }
        else
        {
            _probability = probability;
            _utility = utility;

            _agent_strategy = "";
            _bandit_strategy = "";
        }
    }

    @Override
    public String toString()
    {
        return String.format("pos %s, as %s, bs %s, prob %f, band %d, dang %d, u %f",
                _pos, _agent_strategy, _bandit_strategy, _probability, _bandit_count, _danger_count, _utility);
    }

    public boolean CellUsed(Vector2D pos)
    {
        if(pos.equals(_pos))
            return true;

        if(_parent != null)
            return _parent.CellUsed(pos);
        return false;
    }

    public Vector2D GetPos() { return _pos; }

    public int GetBanditCount() { return _bandit_count; }
    public int GetDangerCount() { return _danger_count; }
    public float GetProbability() { return _probability; }
    public float GetUtility() { return _utility; }
    public String GetAgentStrategy() { return _agent_strategy; }
    public String GetBanditStrategy() { return _bandit_strategy; }
}
