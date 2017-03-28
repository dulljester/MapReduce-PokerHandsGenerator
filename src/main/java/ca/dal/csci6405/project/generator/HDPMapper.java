package ca.dal.csci6405.project.generator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.*;

/**
 * Created by sj on 27/03/17.
 */

/**
 * given a number "k", generates the k-th lexicographic
 * sample without replacement (i.e. combination)
 * of size 5 -- in other words, a hand; it also can shuffle them
 * so that the hand is more "natural"
 */
class CombinationGenerator {
    public final static int N = 52, M = 5;
    private final static long [][]C = new long[N+1][M+1];
    static {
        /* combinatorial coefficients */
        for ( int i = 0; i <= N; C[i++][0] = 1L );
        for ( int i = 1; i <= N; ++i )
            for ( int j = 1; j <= M; C[i][j] = C[i-1][j]+C[i-1][j-1], ++j ) ;
    }
    private boolean f( List<Integer> res, long k, int cur, int idx ) {
        if ( cur == M )
            return k == 0;
        if ( idx > N || cur > M || C[N-idx][M-cur] <= k ) return false ;
        long sum = 0;
        assert sum <= k;
        for ( int i = idx; i < N; sum += C[N-i][M-cur], ++i )
            if ( sum+C[N-i][M-cur] > k ) {
                res.add(i);
                return f(res,k-sum,cur+1,i+1);
            }
        return false;
    }
    public List<Integer> generateLexCombination( long k ) {
        if ( !(0 <= k && k < C[N][M]) )
            throw new IllegalArgumentException(String.format("Not true: %ld <= %ld <= %ld\n",0L,k,C[N][M]));
        List<Integer> res = new ArrayList<>();
        if ( !f(res,k,0,0) )
            return null;
        return res;
    }
    public List<Integer> generateShuffledCombination( long k ) {
        List<Integer> res = generateLexCombination(k);
        if ( res == null ) return res;
        Collections.shuffle(res);
        return res;
    }
}

enum PokerHandType {

    NOTHING(0) {
        @Override
        public boolean is( int card ) {
            int used = 0;
            for ( int i = 0; i < CombinationGenerator.M; ++i )
                used |= 1<<getRank(getCard(card,i));
            return Integer.bitCount(used) == CombinationGenerator.M;
        }
    },
    ONE_PAIR(1) {
        @Override
        public boolean is( int card ) {
            int used = 0;
            for ( int i = 0; i < CombinationGenerator.M; ++i )
                used |= 1<<getRank(getCard(card,i));
            return Integer.bitCount(used) == CombinationGenerator.M-1;
        }
    },
    TWO_PAIRS(2) {
        @Override
        public boolean is( int card ) {
            Map<Integer,Integer> cnt = new HashMap<>();
            for ( int i = 0; i < CombinationGenerator.M; ++i ) {
                int rank = PokerHandType.getRank(getCard(card,i));
                if ( cnt.containsKey(rank) )
                    cnt.put(rank,cnt.get(rank)+1);
                else cnt.put(rank,1);
            }
            int ax = 0;
            for ( Map.Entry<Integer,Integer> entry: cnt.entrySet() )
                if ( entry.getValue() == 2 && ++ax == 2 )
                    return true ;
            return false ;
        }
    },
    THREE_OF_A_KIND(3) {
        @Override
        public boolean is( int card ) {
            Map<Integer,Integer> cnt = new HashMap<>();
            for ( int i = 0; i < CombinationGenerator.M; ++i ) {
                int rank = PokerHandType.getRank(getCard(card,i));
                if ( cnt.containsKey(rank) )
                    cnt.put(rank,cnt.get(rank)+1);
                else cnt.put(rank,1);
            }
            for ( Map.Entry<Integer,Integer> entry: cnt.entrySet() )
                if ( entry.getValue() == 3 )
                    return true ;
            return false ;
        }
    },
    STRAIGHT(4) {
        @Override
        public boolean is( int card ) {
            int used = 0;
            for ( int i = 0; i < CombinationGenerator.M; ++i )
                used |= 1<<getRank(getCard(card,i));
            if ( Integer.bitCount(used) != CombinationGenerator.M )
                return false ;
            return (used&(used+L(used))) == 0;
        }
    },
    FLUSH(5) {
        @Override
        public boolean is( int card ) {
            int used = 0;
            for ( int i = 0; i < CombinationGenerator.M; ++i )
                used |= 1<<getSuit(getCard(card,i));
            return 0 == (used&(used-1));
        }
    },
    FULL_HOUSE(6) {
        @Override
        public boolean is( int card ) {
            Map<Integer,Integer> cnt = new HashMap<>();
            for ( int i = 0; i < CombinationGenerator.M; ++i ) {
                int rank = PokerHandType.getRank(getCard(card,i));
                if ( cnt.containsKey(rank) )
                    cnt.put(rank,cnt.get(rank)+1);
                else cnt.put(rank,1);
            }
            int ax = 0, bx = 0;
            for ( Map.Entry<Integer,Integer> entry: cnt.entrySet() )
                if ( entry.getValue() == 3 ) ++ax;
                else if ( entry.getValue() == 2 ) ++bx;
            return ax == 1 && bx == 1;
        }
    },
    FOUR_OF_A_KIND(7) {
        @Override
        public boolean is( int card ) {
            Map<Integer,Integer> cnt = new HashMap<>();
            for ( int i = 0; i < CombinationGenerator.M; ++i ) {
                int rank = PokerHandType.getRank(getCard(card,i));
                if ( cnt.containsKey(rank) )
                    cnt.put(rank,cnt.get(rank)+1);
                else cnt.put(rank,1);
            }
            for ( Map.Entry<Integer,Integer> entry: cnt.entrySet() )
                if ( entry.getValue() == 4 )
                    return true;
            return false;
        }
    },
    STRAIGHT_FLUSH(8) {
        @Override
        public boolean is( int card ) {
            int usedSuits = 0, usedRanks = 0;
            for ( int i = 0; i < CombinationGenerator.M; ++i ) {
                usedSuits |= 1 << getSuit(getCard(card,i));
                usedRanks |= 1 << getRank(getCard(card,i));
            }
            return (usedRanks & (usedRanks+L(usedRanks))) == 0 && (usedSuits & (usedSuits-1)) == 0;
        }
    },
    ROYAL_FLUSH(9) {
        @Override
        public boolean is( int card ) {
            int usedSuits = 0, usedRanks = 0;
            for ( int i = 0; i < CombinationGenerator.M; ++i ) {
                usedSuits |= 1 << getSuit(getCard(card,i));
                usedRanks |= 1 << getRank(getCard(card,i));
            }
            return (usedRanks & (usedRanks+L(usedRanks))) == 0 && (usedSuits & (usedSuits-1)) == 0 && (usedRanks>>8) == 31;
        }
    };
    private static int L( int k ) {
        return k&((~k)+1);
    }
    private static long BIT( int k ) {
        return (1L<<k);
    }
    private static long MASK( int k ) {
        return BIT(k)-1L;
    }
    private static int getCard( int card, int i ) {
        return (int)((card >> (6*i)) & MASK(6));
    }
    private static int getRank( int card ) {
        return (int)((card>>2)&MASK(4));
    }
    private static int getSuit( int card ) {
        return (card&3);
    }
    private int ord;
    public boolean is( int card ) { return false; };
    PokerHandType( int ord ) {
        this.ord = ord;
    }
    @Override
    public String toString() {
        return Integer.toString(ord);
    }
}

class PokerHandDetector {
    public static PokerHandType detect( int []cards ) {
        int encoding = 0;
        for ( int i = 0; i < CombinationGenerator.M; ++i )
            encoding |= (cards[i]<<(6*i));
        PokerHandType []types = PokerHandType.values();
        for ( int i = types.length-1; i >= 0; --i )
            if ( types[i].is(encoding) )
                return types[i];
        return null;
    }
}

public class HDPMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final IntWritable ONE = new IntWritable(1);
    private final CombinationGenerator generator = new CombinationGenerator();
    private Text word = new Text();
    private int []cards = new int[CombinationGenerator.M];
    public void map( LongWritable key, Text value, final Context con )
            throws IOException, InterruptedException {
        String text = value.toString();
        for ( String line: text.split("\n") ) {
            long k = new Scanner(line).nextLong();
            List<Integer> res = generator.generateLexCombination(k);
            if ( res == null )
                throw new IllegalStateException("Couldn't fine combination with lex number "+k);
            StringBuilder sb = new StringBuilder();
            int m = 0;
            for ( Integer x: res ) {
                assert 0 <= x && x < CombinationGenerator.N;
                int suit = (x/13), rank = (x%13);
                assert suit >= 0 && suit <= 3;
                sb.append((suit+1)+" "+(((rank+1)%13)+1));
                cards[m++] = suit|(rank<<2);
            }
            sb.append(" "+PokerHandDetector.detect(cards).toString());
            word.set(sb.toString());
            con.write(word,ONE);
        }
    }
}

