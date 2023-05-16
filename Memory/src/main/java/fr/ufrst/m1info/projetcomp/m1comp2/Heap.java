package fr.ufrst.m1info.projetcomp.m1comp2;

import fr.ufrst.m1info.projetcomp.m1comp2.ast.commons.Type;

import java.nio.file.FileSystemAlreadyExistsException;
import java.util.*;

public class Heap {
    final private Object[] values;
    ArrayList<HeapEntry> heapEntries;
    HashMap<Integer, List<Integer>> blocks;
    final int heapSize;



    /**
     *  Represents a free block in the heap using both its size and address
     */
    private class BlockInformation {

        int size;
        int address;

        public int getSize() {
            return size;
        }

        public int getAddress() {
            return address;
        }

        BlockInformation(int size, int address) {
            this.size = size;
            this.address = address;
        }
    }

    class SortByAddress implements Comparator<BlockInformation> {
        @Override
        public int compare(BlockInformation o1, BlockInformation o2) {
            return o1.getAddress() - o2.getAddress();
        }
    }

    Heap () throws HeapException{
        this(256);
    }

    Heap (int heapSize) throws HeapException {

        // Check if correct heap size
        if (!correctBlockSize(heapSize)) {
            throw new HeapException("Wrong block size");
        }

        this.heapSize = heapSize;
        values = new Object[heapSize];
        int heapIndexSize = logN(2, heapSize);
        heapEntries = new ArrayList<>();
        blocks = new HashMap<>();
        List<Integer> block = new LinkedList<>();
        block.add(1);
        for (int i = 0; i < heapIndexSize; i++) {
            blocks.put(i, new LinkedList<>());
        }
        blocks.put(heapIndexSize, block);
    }

    // Getters / setters

    public Object[] getValues() {
        return values;
    }

    public ArrayList<HeapEntry> getHeapEntries() {
        return heapEntries;
    }

    /**
     * Get an entry of the heap according to its address
     * @param adr : address of the entry in the heap
     * @return the corresponding entry
     */
    private HeapEntry getHeapEntry(int adr) throws HeapException {
        for(HeapEntry entry : heapEntries) {
            if(entry.getAdr() == adr) {
                return entry;
            }
        }
        throw new HeapException("No heap entry corresponding to the address "+adr);
    }


    public void setHeapEntries(ArrayList<HeapEntry> heapEntries) {
        this.heapEntries = heapEntries;
    }

    public HashMap<Integer, List<Integer>> getBlocks() {
        return blocks;
    }

    public void setBlocks(HashMap<Integer, List<Integer>> blocks) {
        this.blocks = blocks;
    }

    public int getHeapSize() {
        return heapSize;
    }

    // Function for logN(num)
    public int logN(int n, int num) {
        return (int)(Math.log(num) / Math.log(n));
    }

    // Check if the size of a block is correct (power of 2)
    private boolean correctBlockSize (int blockSize) {
        double logHeapSize = (Math.log(blockSize) / Math.log(2));
        return (logHeapSize == (int)logHeapSize);
    }

    private void removeMergedBlocks (List<BlockInformation> blocksToRemove) {

        for (BlockInformation block : blocksToRemove) {
            List<Integer> blockList = blocks.get(logN(2,block.getSize()));
            for (int i = 0; i < blockList.size(); i++) {
                if (blockList.get(i) == block.getAddress()) {
                    blockList.remove(i);
                }
            }
        }
    }

    private void merge (List<List<BlockInformation>> listBlocksToMerge) {

        for (List<BlockInformation> blockList : listBlocksToMerge) {
            // Get size to be merged
            int mergeSize = 0;
            for (BlockInformation block : blockList) {
                mergeSize += block.getSize();
            }

            // Fill memory
            fillMemory(blockList.get(0).getAddress(), mergeSize);

            // Remove each block to create a new block
            removeMergedBlocks(blockList);
        }
    }

    private List<BlockInformation> linearizeMemory () {
        List<BlockInformation> res = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> block : blocks.entrySet()) {
            for (Integer address : block.getValue()) {
                res.add(new BlockInformation((int) Math.pow(2, block.getKey()), address));
            }
        }
        res.sort(new SortByAddress());
        return res;
    }

    private List<List<BlockInformation>> cleanList (List<List<BlockInformation>> rawList) {
        List<List<BlockInformation>> res = new LinkedList<>();
        for (int i = 0; i < rawList.size(); i++) {
            if (rawList.get(i).size() > 1) {
                res.add(rawList.get(i));
            }
        }
        return res;
    }


    public void defragmentation () {

        // Get linearized memory in ascending order
        List<BlockInformation> linearizedMemory = linearizeMemory();

        // Compute blocks to merge
        boolean flag = true;
        int c = -1;
        List<List<BlockInformation>> listBlocksToMerge = new LinkedList<>();

        BlockInformation savedBlock = null;
        for (int i = 1; i < linearizedMemory.size(); i++) {

            BlockInformation previousBlock = linearizedMemory.get(i - 1);
            BlockInformation currBlock = linearizedMemory.get(i);

            if (flag) {

                //Add last encountered block to merge to current list
                if (savedBlock != null) {
                    listBlocksToMerge.get(c).add(savedBlock);
                    savedBlock = null;
                }

                List<BlockInformation> blocksToMerge = new LinkedList<>();
                listBlocksToMerge.add(blocksToMerge);
                flag = false;
                c++;
            }

            if (previousBlock.getAddress() + previousBlock.getSize() == currBlock.getAddress()) {
                savedBlock = currBlock;
                listBlocksToMerge.get(c).add(previousBlock);
            }
            else {
                flag = true;
            }
        }

        if (linearizedMemory.size() == 0) {
            return;
        }
        BlockInformation lastBlock = linearizedMemory.get(linearizedMemory.size() - 1);
        BlockInformation beforeLastBlock = linearizedMemory.get(linearizedMemory.size() - 2);

        if (beforeLastBlock.getAddress() + beforeLastBlock.getSize() == lastBlock.getAddress()) {
            listBlocksToMerge.get(c).add(lastBlock);
        }

        // Remove empty/single objects lists
        listBlocksToMerge = cleanList(listBlocksToMerge);

        // Merge blocks
        merge(listBlocksToMerge);
    }

    // Divide a block by two smaller equals blocks
    private void divideBlock(int adr, int blockSize) {

        int indexToLook = logN(2, blockSize);
        List<Integer> list = blocks.get(indexToLook);
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i) == adr) {
                list.remove(i);
                int newBlockSize = blockSize / 2;
                int newIndexToLook = logN(2, newBlockSize);
                List<Integer> newBlockList = new LinkedList<>();
                // First new block
                newBlockList.add(adr);
                // Second new block
                newBlockList.add(adr + newBlockSize);
                blocks.put(newIndexToLook, newBlockList);
                break;
            }
        }
    }

    // Shifts all the blocks to join memory
    private void reorderBlocks() {
        HashMap<Integer, List<Integer>> tempBlocks = new HashMap<>();
        int heapIndexSize = logN(2, heapSize);
        for (int i = 0; i <= heapIndexSize; i++) {
            tempBlocks.put(i, new LinkedList<>());
        }
        int adrToPut = 1;
        // Reasign the blocks adr
        for (Map.Entry<Integer, List<Integer>> sizeEntry : blocks.entrySet()) {
            for (int adr : sizeEntry.getValue()) {
                int size = sizeEntry.getKey();
                tempBlocks.get(size).add(adrToPut);
                adrToPut += Math.pow(size, 2);
            }
        }
        // Reasign the blocks taken adr
        for (HeapEntry heapEntry : heapEntries) {
            int size = heapEntry.getSize();
            heapEntry.setAdr(adrToPut);
            adrToPut += size;
        }
        blocks = tempBlocks;
    }

    // Return the size of the smaller block which fit with the size
    private int getMostAccurateBlock(int currSize) throws HeapException {
        // If there is no more memory to allocate
        if (currSize > heapSize) {
            return -1;
        }
        int indexToLook = logN(2, currSize);
        List<Integer> list = blocks.get(indexToLook);
        if (! list.isEmpty()) {
            return currSize;
        } else {
            return getMostAccurateBlock(currSize * 2);
        }
    }

    // TODO
    private int assignBlock(int size) throws HeapException {
        int indexToLook = logN(2, size);
        List<Integer> list = blocks.get(indexToLook);
        // If there are blocks of exact size
        int adr;
        if (! list.isEmpty()) {
            // We take the first address of the list that comes
            adr = list.get(0);
            // We remove the block in the blocks map because it is taken
            blocks.get(indexToLook).remove(0);
            return adr;
        } else {
            // Else we must search the most accurate block and cut it
            int newSize = getMostAccurateBlock(size * 2);
            // If we have not enough memory we reorder the blocks
            if (newSize == -1) {
                reorderBlocks();
                defragmentation();
                newSize = getMostAccurateBlock(size);
                if (newSize == -1) {
                    throw new HeapException("Not enough memory for the heap");
                }
            }
            indexToLook = logN(2, newSize);
            list = blocks.get(indexToLook);
            adr = list.get(0);
            // We cut the block until the size was correct
            while (newSize != size) {
                divideBlock(adr, newSize);
                newSize /= 2;
            }
            indexToLook = logN(2, newSize);
            blocks.get(indexToLook).remove(0);
        }
        return adr;
    }

    // Get the most accurate size upper for a size (ex: 16 for 11)
    private int getMostAccurateSizeUpper(int size) {
        int currSize = 1;
        while (currSize < size) {
            currSize *= 2;
        }
        return currSize;
    }

    // Get the most accurate size lower for a size (ex: 8 for 11)
    private int getMostAccurateSizeLower(int size) {
        int currSize = 1;
        while (currSize < size) {
            currSize *= 2;
        }
        if (currSize == size) {
            return currSize;
        }
        return currSize / 2;
    }

    // Take the necessary size in a power of 2 block, and free the rest
    private void takeNecessary(int adr, int neededSize, int totalSize) {
        int rest = totalSize - neededSize;
        if (rest == 0) {
            return;
        }
        int reallocSize = getMostAccurateSizeLower(rest);
        int indexToRealloc = logN(2, reallocSize);
        int adrToRealloc = adr + neededSize;
        blocks.get(indexToRealloc).add(adrToRealloc);
        takeNecessary(adr, neededSize + reallocSize, totalSize);
    }

    // Return the address where the array was created
    public int allocateMemory(int size, Type type) throws HeapException {
        // If the size is too big for the heap size
        if (size > heapSize) {
            throw new HeapException("The memory to allocate ("+size+") is too big for a "+heapSize+" heap memory");
        }
        int exactSize = size;
        // Check if correct block size
        if (!correctBlockSize(size)) {
           size = getMostAccurateSizeUpper(size);
        }
        int adr = assignBlock(size);
        takeNecessary(adr, exactSize, size);

        Object initialValue = (type.equals(Type.INTEGER))? 0 : false;

        // Initialize all value of the given array (to 0 or false)
        for(int i = adr, l = adr+size-1; i < l ; ++i){
            values[i] = initialValue;
        }

        //defragmentation();
        heapEntries.add(new HeapEntry(adr, exactSize, 1)); // exactSize replace size ?
        return adr;
    }


    private void fillMemory(int adr, int size) {

        int power = logN(2, size);
        blocks.get(power).add(adr);

        int targetAddress = adr + size;
        int nextAddress = adr + (int) Math.pow(2,power);


        while (nextAddress < targetAddress) {
            power -= 1;
            int addressGap = (int) Math.pow(2,power);
            if (nextAddress + addressGap <= targetAddress) {
                blocks.get(power).add(nextAddress);
                nextAddress += addressGap;
            }
        }
    }

    public void freeBlock(int adr) throws HeapException {

        // Erase entry of heap's symbol table
        HeapEntry entry = getHeapEntry(adr);

        int entrySize = entry.getSize();
        heapEntries.remove(entry);

        // Fill lost memory
        defragmentation();
        fillMemory(adr,entrySize);
    }

    // Increment by one the counter at the given address
    public void incRef(int adr) {
        for (HeapEntry entry : this.heapEntries) {
            if (entry.getAdr() == adr) {
                entry.incRef();
            }
        }
    }

    // Decrement by one the counter at the given address (delete if the entry was empty or equal to 0)
    public void decRef(int adr) throws HeapException {
        HeapEntry e = null;
        for (HeapEntry entry : this.heapEntries) {
            if (entry.getAdr() == adr && e == null) {
                entry.decRef();
                e = entry;
                break;
            }
        }
        if(e != null && e.getRef() == 0){
            freeBlock(adr);
        }
    }

    // Set the address value at the given array index
    public void setArrayValueAt(int adr, int index, Object value) throws HeapException {
        HeapEntry entry = getHeapEntry(adr);
        if(!isValidIndex(entry,index)){
            throw new HeapException("Try to set array value at an non-valid index "+index);
        }
        values[adr + index] = value;
    }

    // Return the value at the given array index and address
    public Object getArrayValueAt(int adr, int index) throws HeapException {
        HeapEntry entry = getHeapEntry(adr);
        if(!isValidIndex(entry,index)){
            throw new HeapException("Try to get array value at an non-valid index "+index);
        }
        return values[adr + index];
    }

    public int getEntryLength(int adr) throws HeapException {
        return getHeapEntry(adr).getSize();
    }

    private boolean isValidIndex(HeapEntry entry, int index){
        return !(index < 0 || index >= entry.getSize());
    }
}
