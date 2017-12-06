package ekimoiva;

import ekimoiva.Map.CMap;
import ekimoiva.Map.CMapCell;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Main
{
    static ArrayList<String> _ex1 = CreateExample1();
    static ArrayList<String> _ex2 = CreateExample2();
    static ArrayList<String> _ex3 = CreateExample3();
    static int _line_count = 0;

    static ArrayList<String> CreateExample1()
    {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("7");
        lst.add("9");
        lst.add("#########");
        lst.add("#G-----E#");
        lst.add("#-#####-#");
        lst.add("#S--E--D#");
        lst.add("#-#####-#");
        lst.add("#G-----E#");
        lst.add("#########");
        lst.add("2");
        lst.add("0.5");
        return lst;
    }

    static ArrayList<String> CreateExample2()
    {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("7");
        lst.add("9");
        lst.add("#########");
        lst.add("#G-E---E#");
        lst.add("#-#####-#");
        lst.add("#S-E--ED#");
        lst.add("#-#####-#");
        lst.add("#G-E---E#");
        lst.add("#########");
        lst.add("2");
        lst.add("0.6");
        return lst;
    }

    static ArrayList<String> CreateExample3()
    {
        ArrayList<String> lst = new ArrayList<>();
        lst.add("7");
        lst.add("9");
        lst.add("#########");
        lst.add("#E----EG#");
        lst.add("#-##-##E#");
        lst.add("#S##-##-#");
        lst.add("#-##-##-#");
        lst.add("#E-----D#");
        lst.add("#########");
        lst.add("2");
        lst.add("0.5");
        return lst;
    }

    static String ReadLineFromExample1()
    {
        if(_line_count >= _ex1.size())
            return "";

        String s = _ex1.get(_line_count);
        _line_count++;
        return s;
    }

    static String ReadLineFromExample2()
    {
        if(_line_count >= _ex2.size())
            return "";

        String s = _ex2.get(_line_count);
        _line_count++;
        return s;
    }

    static String ReadLineFromExample3()
    {
        if(_line_count >= _ex3.size())
            return "";

        String s = _ex3.get(_line_count);
        _line_count++;
        return s;
    }

    static String ReadLineFromStd() throws IOException
    {
        StringBuilder sb = new StringBuilder();

        int ch = System.in.read();

        while (ch != -1 && ch != '\n')
        {
            sb.append((char)ch);
            ch = System.in.read();
        }

        return sb.toString();
    }

    static String ReadLine(String[] args) throws IOException
    {
        String line;
        if(args.length > 0 && "1".equals(args[0]))
            line = ReadLineFromExample1();
        else if(args.length > 0 && "2".equals(args[0]))
            line = ReadLineFromExample2();
        else if(args.length > 0 && "3".equals(args[0]))
            line = ReadLineFromExample3();
        else
            line = ReadLineFromStd();
        return line;
    }

    public static void main(String[] args) throws IOException
    {
        String line = ReadLine(args);
        int line_count = -2;

        int map_h = 0;
        int map_w = 0;

        CMap map = null;

        int bandit_count = 0;
        int danger_count = 0;
        float kill_probability = 0;
        Vector2D start_pos = null;

        while(!line.isEmpty())
        {
            System.out.printf("line: %s\n", line);

            if(line_count == -2)
                map_h = Integer.parseInt(line);
            else if(line_count == -1)
            {
                map_w = Integer.parseInt(line);
                map = new CMap(map_w, map_h);
            }
            else if(line_count < map_h)
            {
                char[] chars = line.toCharArray();
                for(int i = 0; i < chars.length; i++)
                {
                    char t = chars[i];
                    if(t == 'S')
                        start_pos = new Vector2D(i, line_count);
                    else
                    {
                        map.SetCellType(i, line_count, t);
                        if(t == 'E')
                            danger_count++;
                    }
                }
            }
            else if(line_count < map_h + 1)
                bandit_count = Integer.parseInt(line);
            else if(line_count < map_h + 2)
                kill_probability = Float.parseFloat(line);

            line = ReadLine(args);
            line_count++;
        }

        CNode root = new CNode(start_pos, null, bandit_count, danger_count, 1, 0, (char)0, (char)0);
        CreateTree(root, map, kill_probability);
    }

    static void CreateTree(CNode root, CMap map, float kill_probability)
    {
        System.out.printf("\n");
        ArrayList<CStrategiesUtil> strategies = new ArrayList<>();

        Stack<CNode> node_stack = new Stack<>();
        node_stack.push(root);

        while(!node_stack.empty())
        {
            CNode node = node_stack.pop();

            Vector2D pos = node.GetPos();

            boolean was_added = false;
            Pair<Character, Vector2D>[] nposes = pos.GetNeighbours(map.GetMapRect());
            for(Pair<Character, Vector2D> p: nposes)
            {
                if(!node.CellUsed(p.getValue()))
                {
                    CMapCell cell = map.GetCell(p.getValue());
                    if(cell.IsPassable())
                    {
                        CNode child_node = null;
                        switch(cell.GetCellType())
                        {
                            case Exit:
                            {
                                CStrategiesUtil u = new CStrategiesUtil(node.GetAgentStrategy() + p.getKey(),
                                                                        node.GetBanditStrategy(),
                                                                    (node.GetUtility() + 10) * node.GetProbability());
                                strategies.add(u);
                                int sz = strategies.size();
                                System.out.printf("%d: %s\n", sz, u);
                                was_added = true;
                            }
                            break;
                            case Danger:
                            {
                                if(node.GetBanditCount() > 0)
                                {
                                    was_added = true;

                                    //kill
                                    CStrategiesUtil u = new CStrategiesUtil(node.GetAgentStrategy() + p.getKey(), node.GetBanditStrategy() + 'k', 0);
                                    strategies.add(u);
                                    int sz = strategies.size();
                                    System.out.printf("%d: %s\n", sz, u);

                                    child_node = new CNode(p.getValue(), node, node.GetBanditCount(), node.GetDangerCount() - 1, 1, 0, p.getKey(), (char) 0);

                                    float was_bandit_probability = node.GetBanditCount() / (float) node.GetDangerCount();

                                    //miss
                                    float was_bandit_miss_probability = was_bandit_probability * (1 - kill_probability);
                                    CNode bandit_node = new CNode(p.getValue(), child_node, child_node.GetBanditCount() - 1, child_node.GetDangerCount(),
                                            was_bandit_miss_probability, 0, (char) 0, 'm');
                                    node_stack.push(bandit_node);

                                    //bandit absent
                                    float was_not_bandit_probability = 1 - was_bandit_probability;
                                    bandit_node = new CNode(p.getValue(), child_node, child_node.GetBanditCount(), child_node.GetDangerCount(),
                                            was_not_bandit_probability, 0, (char) 0, 'a');
                                    node_stack.push(bandit_node);
                                } else
                                {
                                    child_node = new CNode(p.getValue(), node, node.GetBanditCount(), node.GetDangerCount(), 1, 0, p.getKey(), (char) 0);
                                    node_stack.push(child_node);
                                    was_added = true;
                                }
                            }
                            break;
                            case Gold:
                            {
                                child_node = new CNode(p.getValue(), node, node.GetBanditCount(), node.GetDangerCount(), 1, 1, p.getKey(), (char) 0);
                                node_stack.push(child_node);
                                was_added = true;
                            }
                            break;
                            case Empty:
                            {
                                child_node = new CNode(p.getValue(), node, node.GetBanditCount(), node.GetDangerCount(), 1, 0, p.getKey(), (char) 0);
                                node_stack.push(child_node);
                                was_added = true;
                            }
                            break;
                        }
                    }
                }
            }

            if(!was_added)
            {
                CStrategiesUtil u = new CStrategiesUtil(node.GetAgentStrategy(), node.GetBanditStrategy(), 0);
                strategies.add(u);
                int sz = strategies.size();
                System.out.printf("%d: %s\n", sz, u);
            }
        }
    }
}
