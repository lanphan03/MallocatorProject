import java.io.*;
import java.util.*;

public class MemoryAllocator {
    static class MemoryBlock {
        int start, end, size;

        MemoryBlock(int start, int end) {
            this.start = start;
            this.end = end;
            this.size = end - start;
        }
    }

    static class Process {
        int id, size;

        Process(int id, int size) {
            this.id = id;
            this.size = size;
        }
    }

    public static void main(String[] args) throws IOException {
        List<MemoryBlock> memoryBlocks = readMemoryBlocks("Minput.data");
        List<Process> processes = readProcessData("Pinput.data");

        allocateMemory("First-Fit", memoryBlocks, processes, "FFoutput.data");
        allocateMemory("Best-Fit", memoryBlocks, processes, "BFoutput.data");
        allocateMemory("Worst-Fit", memoryBlocks, processes, "WFoutput.data");
    }

    private static List<MemoryBlock> readMemoryBlocks(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<MemoryBlock> blocks = new ArrayList<>();
        int count = Integer.parseInt(reader.readLine().trim());
        for (int i = 0; i < count; i++) {
            String[] values = reader.readLine().split(" ");
            blocks.add(new MemoryBlock(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
        }
        reader.close();
        return blocks;
    }

    private static List<Process> readProcessData(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<Process> processes = new ArrayList<>();
        int count = Integer.parseInt(reader.readLine().trim());
        for (int i = 0; i < count; i++) {
            String[] values = reader.readLine().split(" ");
            processes.add(new Process(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
        }
        reader.close();
        return processes;
    }

    private static void allocateMemory(String method, List<MemoryBlock> memoryBlocks, List<Process> processes, String outputFile) throws IOException {
        List<MemoryBlock> blocks = new ArrayList<>();
        for (MemoryBlock mb : memoryBlocks) {
            blocks.add(new MemoryBlock(mb.start, mb.end)); 
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        List<Integer> unallocatedProcesses = new ArrayList<>();

        for (Process process : processes) {
            int allocatedStart = -1, allocatedEnd = -1;
            MemoryBlock chosenBlock = null;

            if (method.equals("First-Fit")) {
                for (MemoryBlock block : blocks) {
                    if (block.size >= process.size) {
                        chosenBlock = block;
                        break;
                    }
                }
            } else if (method.equals("Best-Fit")) {
                for (MemoryBlock block : blocks) {
                    if (block.size >= process.size) {
                        if (chosenBlock == null || block.size < chosenBlock.size) {
                            chosenBlock = block;
                        }
                    }
                }
            } else if (method.equals("Worst-Fit")) {
                for (MemoryBlock block : blocks) {
                    if (block.size >= process.size) {
                        if (chosenBlock == null || block.size > chosenBlock.size) {
                            chosenBlock = block;
                        }
                }
            }

            if (chosenBlock != null) {
                allocatedStart = chosenBlock.start;
                allocatedEnd = allocatedStart + process.size;
                chosenBlock.start += process.size;
                chosenBlock.size -= process.size;
                writer.write(allocatedStart + " " + allocatedEnd + " " + process.id + "\n");
            } else {
                unallocatedProcesses.add(process.id);
            }
        }

        if (unallocatedProcesses.isEmpty()) {
            writer.write("-0\n");
        } else {
            writer.write("-" + String.join(",", unallocatedProcesses.stream().map(String::valueOf).toArray(String[]::new)) + "\n");
        }

        writer.close();
        }
    }
}


