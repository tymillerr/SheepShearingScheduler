//tymiller

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class SheepShearingScheduler {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter the name of the sheep scheduling file: ");
            String fileName = scanner.nextLine();
            
            List<Sheep> sheepList = readSheepFile(fileName);
            if (sheepList == null) {
                System.out.println("Error reading file. Please try again.");
                continue;
            }
            
            scheduleShearing(sheepList);
            
            System.out.print("Would you like to run it again? (y/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                break;
            }
        }
        scanner.close();
    }

    private static List<Sheep> readSheepFile(String fileName) {
        List<Sheep> sheepList = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\t");
                if (parts.length != 3) continue;

                String name = parts[0];
                int shearTime = Integer.parseInt(parts[1]);
                int arrivalTime = Integer.parseInt(parts[2]);
                sheepList.add(new Sheep(name, shearTime, arrivalTime));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            return null;
        }
        return sheepList;
    }

    private static void scheduleShearing(List<Sheep> sheepList) {
        sheepList.sort(Comparator.comparingInt(Sheep::getArrivalTime));
        MinHeap<Sheep> waitHeap = new MinHeap<>();
        int currentTime = 0;

        System.out.println("Shearing Schedule:");
        for (Sheep sheep : sheepList) {
            while (!waitHeap.isEmpty() && waitHeap.peek().getArrivalTime() <= currentTime) {
                Sheep shearedSheep = waitHeap.remove();
                currentTime += shearedSheep.getShearingTime();
                System.out.printf("Name: %s, Shear Time: %d, Arrival Time: %d\n",
                        shearedSheep.getName(), shearedSheep.getShearingTime(), shearedSheep.getArrivalTime());
            }

            if (sheep.getArrivalTime() > currentTime) {
                currentTime = sheep.getArrivalTime();
            }
            
            waitHeap.add(sheep);
        }
        
        while (!waitHeap.isEmpty()) {
            Sheep shearedSheep = waitHeap.remove();
            currentTime += shearedSheep.getShearingTime();
            System.out.printf("Name: %s, Shear Time: %d, Arrival Time: %d\n",
                    shearedSheep.getName(), shearedSheep.getShearingTime(), shearedSheep.getArrivalTime());
        }
    }
}

class Sheep implements Comparable<Sheep> {
    private String name;
    private int shearingTime;
    private int arrivalTime;

    public Sheep(String name, int shearingTime, int arrivalTime) {
        this.name = name;
        this.shearingTime = shearingTime;
        this.arrivalTime = arrivalTime;
    }

    public String getName() {
        return name;
    }

    public int getShearingTime() {
        return shearingTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public int compareTo(Sheep other) {
        if (this.shearingTime != other.shearingTime) {
            return Integer.compare(this.shearingTime, other.shearingTime);
        }
        return this.name.compareTo(other.name);
    }
}

class MinHeap<T extends Comparable<T>> {
    private List<T> heap;

    public MinHeap() {
        heap = new ArrayList<>();
    }

    public void add(T item) {
        heap.add(item);
        siftUp(heap.size() - 1);
    }

    public T remove() {
        if (isEmpty()) return null;  // Return null if the heap is empty
        T removedItem = heap.get(0);
        T lastItem = heap.remove(heap.size() - 1);
        
        if (!isEmpty()) {  // Only set the root if the heap is not empty after removal
            heap.set(0, lastItem);
            siftDown(0);
        }
        
        return removedItem;
    }

    public T peek() {
        return isEmpty() ? null : heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parentIndex)) >= 0) break;
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void siftDown(int index) {
        while (index < heap.size() / 2) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallerChild = leftChild;

            if (rightChild < heap.size() && heap.get(rightChild).compareTo(heap.get(leftChild)) < 0) {
                smallerChild = rightChild;
            }

            if (heap.get(index).compareTo(heap.get(smallerChild)) <= 0) break;
            swap(index, smallerChild);
            index = smallerChild;
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
