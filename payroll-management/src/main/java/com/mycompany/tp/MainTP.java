package com.mycompany.tp;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainTP {
    public static void main(String[] args) {
        UserData u1 = new UserData(20L);
        UserData u2 = new UserData(null);
        UserData u3 = new UserData(30L);
        UserData u4 = new UserData(40L);

        Map<String, UserData>[] map = new Map[3];
        map[0] = new HashMap<>();
        map[1] = new HashMap<>();

        map[0].put("65347987523452",u1);
        map[0].put(null,null);
        map[0].put("65727554285",null);

        map[1].put("65372523",u3);
        map[1].put("7589248590",u2);
        map[1].put("65347987523452", u4);

        map[2] = null;

        visitCount(map).entrySet().forEach(System.out::println);


//        Base s = new Base.BaseBuilder().setI(23).setI(34).build();
//        System.out.println(s.getI()+" "+s.getJ());

        BaseExtend e = new BaseExtend.BaseExtendBuilder().setI(3).setJ(4).setK(3).build();
        System.out.println(e.getK());
        Base b = e;
        System.out.println(b.getJ()+" "+((BaseExtend) b).getK());

        TreeSet<Integer> t = new TreeSet<>();
        t.add(2);
        t.add(1);
        t.add(3);   
        System.out.println(t.first());

        A a = new ABuilder().setI(2).setJ(3).setI(45).build();
        System.out.println(a.i+" "+a.j);

        List<Map.Entry<String, Long>> sorted = Arrays.stream("aabbccdddeff".split(""))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((o1, o2) ->
                        (int)(o1.getValue() - o2.getValue()))
                .toList();

        long max = sorted.get(sorted.size()-1).getValue();
        long min = sorted.get(0).getValue();
        if(max!=sorted.get(sorted.size()-2).getValue()) Stream.generate(()->sorted.get(sorted.size()-1).getKey()).limit(sorted.get(sorted.size()-1).getValue()).forEach(System.out::println);
        if(min!=sorted.get(1).getValue()) Stream.generate(()->sorted.get(0).getKey()).limit(sorted.get(0).getValue()).forEach(System.out::print);
//        sorted.forEach(System.out::println);
    }

    public static Map<Long, Long> visitCount(Map<String, UserData>[] visits) {
        Map<Long, Long> res = new HashMap<>();
        if(visits==null)
            return res;
        for(Map<String , UserData> m:visits)
        {
            if(m==null)
                continue;

            Map<Long, Long> collect = m.entrySet().stream()
                    //For key to be parsable to long and entry not null
                    .filter(e -> e!=null && e.getKey() != null && parsable(e.getKey()))
                    //For count to be not null
                    .filter(e -> e.getValue()!=null && e.getValue().getCount().isPresent())
                    //Creating map
                    .collect(Collectors.toMap(
                            e -> Long.parseLong(e.getKey()), //Key -> Str to Long
                            e -> e.getValue().getCount().get() // Optional value extraction
                        )
                    );

            for(Long key:collect.keySet())
                res.put(key, res.getOrDefault(key, 0L)+collect.get(key));
        }
        return res;
    }

    private static boolean parsable(String s) {
        for(char c: s.toCharArray())
        {
            if(!Character.isDigit(c))
                return false;
        }
        return true;
    }
}


class UserData {
    private Long count;

    public UserData(Long count) {
        this.count = count;
    }

    public Optional<Long> getCount() {
        return Optional.ofNullable(count);
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

abstract class Base {
    private int i;
    private int j;


    public Base(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public static abstract class BaseBuilder {
        protected int i;
        protected int j;

        public abstract BaseBuilder setJ(int j);

        public abstract BaseBuilder setI(int i);

        public abstract Base build();
    }
}

class BaseExtend extends Base {

    private int k;

    public BaseExtend(int i, int j, int k) {
        super(i, j);
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public static class BaseExtendBuilder extends BaseBuilder {
        private int k;

        public BaseExtendBuilder setK(int k) {
            this.k = k;
            return this;
        }

        @Override
        public BaseExtendBuilder setJ(int j) {
            super.j = j;
            return this;
        }

        @Override
        public BaseExtendBuilder setI(int i) {
            super.i = i;
            return this;
        }

        @Override
        public BaseExtend build() {
            return new BaseExtend(i,j,k);
        }
    }
}

class A {
    int i;
    int j;

    public A(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}

class ABuilder {
    int i;
    int j;

    public ABuilder setI(int i) {
        this.i = i;
        return this;
    }

    public ABuilder setJ(int j) {
        this.j = j;
        return this;
    }

    public A build() {
        return new A(i,j);
    }
}
