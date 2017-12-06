package ekimoiva;

import ekimoiva.Map.CMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    static ArrayList<String> _ex1 = CreateExample1();
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

    static String ReadLineFromExample1()
    {
        if(_line_count >= _ex1.size())
            return "";

        String s = _ex1.get(_line_count);
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
        float kill_probability = 0;

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
                   map.SetCellType(i, line_count, chars[i]);
            }
            else if(line_count < map_h + 1)
                bandit_count = Integer.parseInt(line);
            else if(line_count < map_h + 2)
                kill_probability = Float.parseFloat(line);

            line = ReadLine(args);
            line_count++;
        }
    }
}
