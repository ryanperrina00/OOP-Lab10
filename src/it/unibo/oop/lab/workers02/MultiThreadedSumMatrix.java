package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;


public class MultiThreadedSumMatrix implements SumMatrix{

    private final int nthread;
    
    public MultiThreadedSumMatrix(final int nthread) {
        super();
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
      
        private final int nelem;
        private final List<Double> list;
        private double sum;
        private int startpos;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         * @param matrix
         *            the new matrix to sum
         */
        Worker(final List<Double> list, final int startpos, final int nelem) {
            super();
            this.startpos = startpos;
            this.nelem = nelem;
            this.list = list;
            this.sum = 0.0;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.sum += list.get(i);
            }
        }
        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.sum;
        }
    }

        public List<Double> changeToVector(final double[][] matrix) {
            final List<Double> list = new ArrayList<>();
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    list.add(matrix[i][j]);
                }
          
            }
            return list;
        }
    
       public double sum (final double [][] matrix) {
           List<Double> list = changeToVector(matrix);
           final int size = list.size() % nthread + list.size() / nthread;
           /*
            * Build a list of workers
            */
           final   List<Worker> workers = new ArrayList<>(nthread);
           for (int start = 0; start < list.size(); start += size) {
               workers.add(new Worker(list, start, size));
           }
           /*
            * Start them
            */
           for (final Worker w: workers) {
               w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
}
