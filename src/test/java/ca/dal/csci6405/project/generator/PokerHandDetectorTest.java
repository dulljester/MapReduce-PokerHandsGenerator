package ca.dal.csci6405.project.generator;

import junit.framework.TestCase;

/**
 * Created by sj on 28/03/17.
 */
public class PokerHandDetectorTest extends TestCase {
    private final static int T = 10, J = 11, Q = 12, K = 13, A = 1;
    private int [] getCards( int []rank, int []suit ) {
        int []c = new int[5];
        for ( int i = 0; i < 5; c[i] = (suit[i]-1)|((rank[i]-2==-1?12:rank[i]-2)<<2), ++i );
        return c;
    }
    public void test001() {
        int []rank = {2,4,8,Q,A}, suit = {1,2,3,4,1};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.NOTHING );
    }
    public void test002() {
        int []rank = {4,8,9,K,K}, suit = {1,2,3,4,1};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.ONE_PAIR );
    }
    public void test003() {
        int []rank = {4,7,7,J,J}, suit = {1,2,3,4,1};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.TWO_PAIRS );
    }
    public void test004() {
        int []rank = {6,2,T,T,T}, suit = {1,1,1,2,3};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.THREE_OF_A_KIND );
    }
    public void test005() {
        int []rank = {5,6,7,8,9}, suit = {2,1,2,4,1};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.STRAIGHT );
    }
    public void test006() {
        int []rank = {5,8,T,J,A}, suit = {2,2,2,2,2};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.FLUSH );
    }
    public void test007() {
        int []rank = {J,J,J,K,K}, suit = {1,3,2,4,3};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.FULL_HOUSE );
    }
    public void test008() {
        int []rank = {2,Q,Q,Q,Q}, suit = {2,3,4,2,1};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.FOUR_OF_A_KIND );
    }
    public void test009() {
        int []rank = {5,6,7,8,9}, suit = {3,3,3,3,3};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.STRAIGHT_FLUSH );
    }
    public void test010() {
        int []rank = {T,J,Q,K,A}, suit = {4,4,4,4,4};
        assertEquals( PokerHandDetector.detect(getCards(rank,suit)), PokerHandType.ROYAL_FLUSH );
    }
}
