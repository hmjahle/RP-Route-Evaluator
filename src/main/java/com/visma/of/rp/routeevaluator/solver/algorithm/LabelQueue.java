package com.visma.of.rp.routeevaluator.solver.algorithm;

/**
 * The label queue class is used within the labelling algorithm to handle the current best labels, i.e., the next
 * label to be extended. The queue is a priority queue such that the first label is the "best" label according to the
 * label comparator.
 * When adding labels to the queue it tries to remove closed labels if possible. This will only happen if they are at the end
 * of the queue. Hence there is no guarantee if it will happen or how many there will be removed.
 */
public class LabelQueue {

    private Label[] elements;
    private int elementCnt;

    /**
     * Creates a label queue with standard capacity of 7.
     */
    public LabelQueue() {
        this(7);
    }

    /**
     * Creates a label queue with desired capacity.
     *
     * @param labelCapacity Desired label capacity.
     */
    public LabelQueue(int labelCapacity) {
        elements = new Label[labelCapacity];
    }

    /**
     * Whether the queue contain any labels.
     *
     * @return True if it is empty, otherwise false.
     */
    public boolean isEmpty() {
        return elementCnt == 0;
    }

    /**
     * Number of labels in the queue.
     *
     * @return Integer number of labels.
     */
    public int size() {
        return elementCnt;
    }

    /**
     * Clears the queue by setting the size of the queue to zero.
     */
    public void clear() {
        elementCnt = 0;
    }

    /**
     * Adds a new label to the queue and sorts the queue accordingly.
     *
     * @param label The label to be added to the queue
     */
    public void addLabel(Label label) {
        int insertPosition = findPosition();
        elementCnt = insertPosition + 1;
        updateArrayLength(elementCnt);
        elements[insertPosition] = label;
        sortUp(insertPosition);
    }

    /**
     * Finds the new position for the label. If more than two elements on the queue it checks if
     * there are closed labels at the end at the queue and replace them.
     *
     * @return The initial position in the queue.
     */
    private int findPosition() {
        if (elementCnt <= 2)
            return elementCnt;

        int insertPosition = elementCnt;
        while ((insertPosition > 0) && elements[insertPosition - 1].isClosed()) {
            insertPosition--;
        }
        return insertPosition;
    }

    /**
     * Return the best label from the queue.
     *
     * @return Label or null if the queue is empty.
     */
    public Label poll() {
        if (isEmpty())
            return null;
        Label result = elements[0];
        elementCnt--;
        if (elementCnt == 0)
            return result;
        Label moved = elements[elementCnt];
        elements[0] = moved;
        if (elementCnt > 1)
            sortDown(moved);
        return result;
    }


    private int compare(Label label1, Label label2) {
        return label1.compareTo(label2);
    }


    private void sortUp(int childIndex) {
        Label toBeInserted = elements[childIndex];
        int parentIndex;
        while (childIndex > 0) {
            parentIndex = (childIndex - 1) >> 1;
            Label parent = elements[parentIndex];
            if (compare(parent, toBeInserted) <= 0) {
                break;
            }
            elements[childIndex] = parent;
            childIndex = parentIndex;
        }
        elements[childIndex] = toBeInserted;
    }


    private void sortDown(Label target) {
        int startPosition = 0;
        int childIndex;
        while ((childIndex = (startPosition << 1) + 1) < elementCnt) {
            if (childIndex + 1 < elementCnt && compare(elements[childIndex + 1], elements[childIndex]) < 0) {
                childIndex++;
            }
            if (compare(target, elements[childIndex]) <= 0)
                break;
            elements[startPosition] = elements[childIndex];
            startPosition = childIndex;
        }
        elements[startPosition] = target;
    }


    private void updateArrayLength(int size) {
        if (size > elements.length) {
            Label[] labels = elements;
            elements = new Label[size * 2];
            System.arraycopy(labels, 0, elements, 0, labels.length);
        }
    }
}