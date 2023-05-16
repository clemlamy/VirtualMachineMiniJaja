package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

public class HeapTest {

    private Heap heap;

    @Before
    public void setUp() throws HeapException {
        heap = new Heap(256);
    }

    public void printHeapEntries() {
        for (HeapEntry he : heap.getHeapEntries()) {
            System.out.println("<adr: "+he.getAdr()+", size: "+he.getSize()+", ref: "+he.getRef()+">");
        }
    }

    @Test
    public void constructorDefaultSize() throws  HeapException{
        Heap differentHeap = new Heap();
        Assert.assertEquals(9, differentHeap.getBlocks().size());
    }

    @Test
    public void constructorCorrectSize() throws  HeapException{
        Heap differentHeap = new Heap(1024);
        Assert.assertEquals(11, differentHeap.getBlocks().size());
    }

    @Test
    public void constructorHeapSizeOne() throws  HeapException{
        Heap differentHeap = new Heap(1);
        Assert.assertEquals(1, differentHeap.getBlocks().size());
    }

    @Test (expected = HeapException.class)
    public void constructorIncorrectSize() throws HeapException {
        new Heap(42);
    }

    @Ignore
    @Test
    public void defragmentationTwoBlocks128 () throws HeapException {
        int addr = heap.allocateMemory(128, Type.INTEGER);
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        heap.freeBlock(addr);
        heap.defragmentation();
        Assert.assertEquals(1, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
    }

    @Ignore
    @Test
    public void defragmentationBeginningBlocksPow2 () throws HeapException {
        int addr1 = heap.allocateMemory(64, Type.INTEGER);
        int addr2 = heap.allocateMemory(64, Type.BOOLEAN);
        heap.allocateMemory(128, Type.INTEGER);
        heap.freeBlock(addr1);
        heap.freeBlock(addr2);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Ignore
    @Test
    public void defragmentationMiddleBlocksPow2 () throws HeapException {
        heap.allocateMemory(128, Type.INTEGER);
        int addr1 = heap.allocateMemory(32, Type.BOOLEAN);
        int addr2 = heap.allocateMemory(32, Type.INTEGER);
        heap.allocateMemory(64, Type.INTEGER);
        heap.freeBlock(addr1);
        heap.freeBlock(addr2);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Ignore
    @Test
    public void defragmentationEndBlocksPow2 () throws HeapException {
        heap.allocateMemory(128, Type.INTEGER);
        int addr = heap.allocateMemory(64, Type.BOOLEAN);
        heap.freeBlock(addr);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Ignore
    @Test
    public void defragmentationBeginningBlocksNonPow2 () throws HeapException {
        int addr1 = heap.allocateMemory(42, Type.INTEGER);
        int addr2 = heap.allocateMemory(42, Type.BOOLEAN);
        heap.allocateMemory(128, Type.INTEGER);
        heap.freeBlock(addr1);
        heap.freeBlock(addr2);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }


    @Test
    public void defragmentationMiddleBlocksNonPow2 () throws HeapException {
        heap.allocateMemory(128,Type.INTEGER);
        int addr = heap.allocateMemory(42, Type.BOOLEAN);
        heap.allocateMemory(64, Type.INTEGER);
        heap.freeBlock(addr);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void defragmentationEndBlocksNonPow2 () throws HeapException {
        heap.allocateMemory(128,Type.INTEGER);
        heap.allocateMemory(64, Type.INTEGER);
        int addr = heap.allocateMemory(42, Type.BOOLEAN);
        heap.freeBlock(addr);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void defragmentationLectureEg () throws HeapException {
        int addr = heap.allocateMemory(2, Type.INTEGER);
        heap.allocateMemory(11, Type.INTEGER);
        heap.freeBlock(addr);
        heap.defragmentation();
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(1, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(1, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(1, heap.getBlocks().get(0).size());
    }

    @Test
    public void defragmentationAllBlocksTaken() throws HeapException {
        heap.allocateMemory(256, Type.INTEGER);
        Heap heap2 = new Heap(256);
        heap2.allocateMemory(256, Type.INTEGER);
        heap2.defragmentation();
        Assert.assertEquals(heap.getBlocks(), heap2.getBlocks());
        Assert.assertEquals(heap.getHeapEntries(), heap2.getHeapEntries());
    }

    @Test
    public void allocateMemory256for256() throws HeapException {
        int adr = heap.allocateMemory(256, Type.INTEGER);
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getHeapEntries().size());
        Assert.assertEquals(256, heap.getHeapEntries().get(0).getSize());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getValues()[adr+42]);
    }

    @Test
    public void allocateMemory256for256Boolean() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getHeapEntries().size());
        Assert.assertEquals(256, heap.getHeapEntries().get(0).getSize());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(false, heap.getValues()[adr+42]);
    }

    @Test
    public void allocateMemory128for256() throws HeapException {
        int adr = heap.allocateMemory(128, Type.INTEGER);
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getHeapEntries().size());
        Assert.assertEquals(128, heap.getHeapEntries().get(0).getSize());
        Assert.assertEquals(0, heap.getValues()[adr+42]);
    }

    @Test
    public void allocateMemory32for256() throws HeapException {
        int adr = heap.allocateMemory(32, Type.INTEGER);
        printHeapEntries();
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getHeapEntries().size());
        Assert.assertEquals(32, heap.getHeapEntries().get(0).getSize());
        Assert.assertEquals(0, heap.getValues()[adr+12]);
    }

    @Test
    public void allocateMemory1for256() throws HeapException {
        heap.allocateMemory(1, Type.INTEGER);
        printHeapEntries();
        Assert.assertEquals(1, heap.getBlocks().get(1).size());
        Assert.assertEquals(1, heap.getBlocks().get(2).size());
        Assert.assertEquals(1, heap.getBlocks().get(3).size());
        Assert.assertEquals(1, heap.getBlocks().get(4).size());
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getHeapEntries().size());
        Assert.assertEquals(1, heap.getHeapEntries().get(0).getSize());
    }

    @Test (expected = HeapException.class)
    public void allocateMemoryNotEnoughMemory() throws HeapException {
        heap.allocateMemory(256, Type.INTEGER);
        heap.allocateMemory(256, Type.INTEGER);
    }

    @Test (expected = HeapException.class)
    public void allocateMemoryTooBigSize() throws HeapException {
        heap.allocateMemory(512, Type.INTEGER);
    }

    @Test
    public void allocateMemoryRandomSizeFor256() throws HeapException {
        int adr = heap.allocateMemory(42, Type.INTEGER);
        Assert.assertEquals(heap.getBlocks().get(1).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(2).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(3).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(4).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(5).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(6).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(7).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(8).size(), 0);
        Assert.assertEquals(heap.getHeapEntries().size(), 1);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 42);
        Assert.assertEquals(0, heap.getValues()[adr+12]);
    }

    @Test
    public void allocateMemorySeveralAllocationsDescending() throws HeapException {
        heap.allocateMemory(64, Type.INTEGER);
        heap.allocateMemory(8, Type.INTEGER);
        heap.allocateMemory(4, Type.INTEGER);
        heap.allocateMemory(2, Type.INTEGER);
        heap.allocateMemory(1, Type.INTEGER);
        heap.allocateMemory(1, Type.INTEGER);
        Assert.assertEquals(heap.getBlocks().get(0).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(1).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(2).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(3).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(4).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(5).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(6).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(7).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(8).size(), 0);
        Assert.assertEquals(heap.getHeapEntries().size(), 6);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 64);
        Assert.assertEquals(heap.getHeapEntries().get(1).getSize(), 8);
        Assert.assertEquals(heap.getHeapEntries().get(2).getSize(), 4);
        Assert.assertEquals(heap.getHeapEntries().get(3).getSize(), 2);
        Assert.assertEquals(heap.getHeapEntries().get(4).getSize(), 1);
        Assert.assertEquals(heap.getHeapEntries().get(5).getSize(), 1);
        printHeapEntries();
    }

    @Test
    public void allocateMemorySeveralAllocationsIncreasing() throws HeapException {
        heap.allocateMemory(1, Type.INTEGER);
        heap.allocateMemory(1, Type.INTEGER);
        heap.allocateMemory(2, Type.INTEGER);
        heap.allocateMemory(4, Type.INTEGER);
        heap.allocateMemory(8, Type.INTEGER);
        heap.allocateMemory(64, Type.INTEGER);
        Assert.assertEquals(heap.getBlocks().get(2).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(3).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(4).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(5).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(6).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(7).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(8).size(), 0);
        Assert.assertEquals(heap.getHeapEntries().size(), 6);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 1);
        Assert.assertEquals(heap.getHeapEntries().get(1).getSize(), 1);
        Assert.assertEquals(heap.getHeapEntries().get(2).getSize(), 2);
        Assert.assertEquals(heap.getHeapEntries().get(3).getSize(), 4);
        Assert.assertEquals(heap.getHeapEntries().get(4).getSize(), 8);
        Assert.assertEquals(heap.getHeapEntries().get(5).getSize(), 64);
        printHeapEntries();
    }

    @Test
    public void allocateMemorySeveralAllocationsRecutOtherParts() throws HeapException {
        heap.allocateMemory(64, Type.INTEGER);
        heap.allocateMemory(8, Type.INTEGER);
        heap.allocateMemory(4, Type.INTEGER);
        heap.allocateMemory(4, Type.INTEGER);
        heap.allocateMemory(8, Type.INTEGER);
        heap.allocateMemory(64, Type.INTEGER);
        Assert.assertEquals(heap.getBlocks().get(2).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(3).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(4).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(5).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(6).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(7).size(), 0);
        Assert.assertEquals(heap.getBlocks().get(8).size(), 0);
        Assert.assertEquals(heap.getHeapEntries().size(), 6);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 64);
        Assert.assertEquals(heap.getHeapEntries().get(1).getSize(), 8);
        Assert.assertEquals(heap.getHeapEntries().get(2).getSize(), 4);
        Assert.assertEquals(heap.getHeapEntries().get(3).getSize(), 4);
        Assert.assertEquals(heap.getHeapEntries().get(4).getSize(), 8);
        Assert.assertEquals(heap.getHeapEntries().get(5).getSize(), 64);
        printHeapEntries();
    }

    @Test
    public void reorderHeapJoinTwoFarBlocks() throws HeapException {
        heap.allocateMemory(124, Type.INTEGER);
        heap.allocateMemory(124, Type.INTEGER);
        heap.allocateMemory(8, Type.INTEGER);
        Assert.assertEquals(heap.getHeapEntries().size(), 3);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 124);
        Assert.assertEquals(heap.getHeapEntries().get(1).getSize(), 124);
        Assert.assertEquals(heap.getHeapEntries().get(2).getSize(), 8);
    }

    @Test
    public void reorderHeapMemoryJoinTwoFarBlocksNotExactSize() throws HeapException {
        heap.allocateMemory(124, Type.INTEGER);
        heap.allocateMemory(124, Type.INTEGER);
        heap.allocateMemory(5, Type.INTEGER);
        Assert.assertEquals(heap.getBlocks().get(0).size(), 1);
        Assert.assertEquals(heap.getBlocks().get(1).size(), 1);
        Assert.assertEquals(heap.getHeapEntries().size(), 3);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 124);
        Assert.assertEquals(heap.getHeapEntries().get(1).getSize(), 124);
        Assert.assertEquals(heap.getHeapEntries().get(2).getSize(), 5);
    }

    @Test
    public void freeBlockWholeHeap() throws HeapException {
        int adr = heap.allocateMemory(256, Type.INTEGER);
        Assert.assertEquals(heap.getBlocks().get(8).size(), 0);
        Assert.assertEquals(heap.getHeapEntries().size(), 1);
        Assert.assertEquals(heap.getHeapEntries().get(0).getSize(), 256);
        Assert.assertEquals(heap.getBlocks().get(8).size(), 0);

        heap.freeBlock(adr);
        Assert.assertEquals(1, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getHeapEntries().size());
    }

    @Ignore
    @Test
    public void freeBlockHalfHeapSize() throws HeapException {
        int adr1 = heap.allocateMemory(128, Type.BOOLEAN);
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        int adr2 = heap.allocateMemory(128, Type.INTEGER);
        Assert.assertEquals(2, heap.getHeapEntries().size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        heap.freeBlock(adr1);
        Assert.assertEquals(1, heap.getHeapEntries().size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        heap.freeBlock(adr2);
        Assert.assertEquals(0, heap.getHeapEntries().size());
        Assert.assertEquals(2, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
    }

    @Test (expected = HeapException.class)
    public void freeBlockAddressDoesntExist() throws HeapException {
        heap.freeBlock(42);
    }

    @Test (expected = HeapException.class)
    public void freeBlockAlreadyFreed() throws HeapException {
        int adr = heap.allocateMemory(256, Type.INTEGER);
        heap.freeBlock(adr);
        heap.freeBlock(adr);
    }

    @Test
    public void freePow2BlockBeginning() throws HeapException {
        int adr = heap.allocateMemory(16, Type.INTEGER);
        Assert.assertEquals(1, heap.getHeapEntries().size());
        heap.freeBlock(adr);
        Assert.assertEquals(0, heap.getHeapEntries().size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(2, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void freeNonPow2BlockBeginning() throws HeapException {
        int adr = heap.allocateMemory(11, Type.INTEGER);
        heap.freeBlock(adr);
        Assert.assertEquals(0, heap.getHeapEntries().size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(1, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(1, heap.getBlocks().get(4).size());
        Assert.assertEquals(1, heap.getBlocks().get(3).size());
        Assert.assertEquals(1, heap.getBlocks().get(2).size());
        Assert.assertEquals(1, heap.getBlocks().get(1).size());
        Assert.assertEquals(2, heap.getBlocks().get(0).size());
    }

    @Test
    public void freePow2Middle() throws HeapException {
        heap.allocateMemory(128, Type.INTEGER);
        int adr = heap.allocateMemory(64, Type.BOOLEAN);
        heap.allocateMemory(64, Type.INTEGER);
        Assert.assertEquals(3, heap.getHeapEntries().size());
        heap.freeBlock(adr);
        Assert.assertEquals(2, heap.getHeapEntries().size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void freeNonPow2Middle() throws HeapException {
        heap.allocateMemory(128, Type.INTEGER);
        int adr = heap.allocateMemory(34, Type.BOOLEAN);
        heap.allocateMemory(64, Type.INTEGER);
        Assert.assertEquals(3, heap.getHeapEntries().size());
        heap.freeBlock(adr);
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(1, heap.getBlocks().get(4).size());
        Assert.assertEquals(1, heap.getBlocks().get(3).size());
        Assert.assertEquals(1, heap.getBlocks().get(2).size());
        Assert.assertEquals(2, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void freePow2BlockEnd() throws HeapException {
        heap.allocateMemory(128, Type.INTEGER);
        heap.allocateMemory(64, Type.BOOLEAN);
        int adr = heap.allocateMemory(64, Type.INTEGER);
        Assert.assertEquals(3, heap.getHeapEntries().size());
        heap.freeBlock(adr);
        Assert.assertEquals(2, heap.getHeapEntries().size());
        Assert.assertEquals(0, heap.getBlocks().get(8).size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(1, heap.getBlocks().get(6).size());
        Assert.assertEquals(0, heap.getBlocks().get(5).size());
        Assert.assertEquals(0, heap.getBlocks().get(4).size());
        Assert.assertEquals(0, heap.getBlocks().get(3).size());
        Assert.assertEquals(0, heap.getBlocks().get(2).size());
        Assert.assertEquals(0, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void freeNonPow2BlockEnd() throws HeapException {
        heap.allocateMemory(128, Type.INTEGER);
        heap.allocateMemory(64, Type.BOOLEAN);
        int adr = heap.allocateMemory(42, Type.INTEGER);
        heap.freeBlock(adr);
        Assert.assertEquals(2, heap.getHeapEntries().size());
        Assert.assertEquals(0, heap.getBlocks().get(7).size());
        Assert.assertEquals(0, heap.getBlocks().get(6).size());
        Assert.assertEquals(1, heap.getBlocks().get(5).size());
        Assert.assertEquals(1, heap.getBlocks().get(4).size());
        Assert.assertEquals(1, heap.getBlocks().get(3).size());
        Assert.assertEquals(1, heap.getBlocks().get(2).size());
        Assert.assertEquals(2, heap.getBlocks().get(1).size());
        Assert.assertEquals(0, heap.getBlocks().get(0).size());
    }

    @Test
    public void getArrayValueAtInvalidIndexNegative() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        Assert.assertThrows(
                HeapException.class,
                () -> heap.getArrayValueAt(adr,-1)
        );
    }

    @Test
    public void getArrayValueAtInvalidIndexTooHigh() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        Assert.assertThrows(
                HeapException.class,
                () -> heap.getArrayValueAt(adr,256)
        );
    }

    @Test
    public void getArrayValueAtValidIndex() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        Assert.assertEquals(false,heap.getArrayValueAt(adr,42));
    }

    @Test
    public void setArrayValueAtInvalidIndexNegative() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        Assert.assertThrows(
                HeapException.class,
                () -> heap.setArrayValueAt(adr,-1,true)
        );
    }

    @Test
    public void setArrayValueAtInvalidIndexTooHigh() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        Assert.assertThrows(
                HeapException.class,
                () -> heap.setArrayValueAt(adr,256,true)
        );
    }

    @Test
    public void setArrayValueAtValidIndex() throws HeapException {
        int adr = heap.allocateMemory(256, Type.BOOLEAN);
        heap.setArrayValueAt(adr,42,true);
        Assert.assertEquals(true,heap.getArrayValueAt(adr,42));
        Assert.assertEquals(false,heap.getArrayValueAt(adr,41));
    }

    @Test
    public void getEntryLength_notEmptyHeap() throws HeapException {
        int adr = heap.allocateMemory(42, Type.BOOLEAN);
        Assert.assertEquals(42, heap.getEntryLength(adr));
    }

    @Test
    public void getEntryLength_emptyHeap_exceptionThrown() throws HeapException {
        Assert.assertThrows(
                HeapException.class,
                () -> heap.getEntryLength(1)
        );
    }
}
