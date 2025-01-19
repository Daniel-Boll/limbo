package org.github.tursodatabase.core;

import java.sql.SQLException;

import org.github.tursodatabase.annotations.Nullable;

/**
 * A table of data representing limbo database result set, which is generated by executing a statement that queries the
 * database.
 * <p>
 * A {@link LimboResultSet} object is automatically closed when the {@link LimboStatement} object that generated it is
 * closed or re-executed.
 */
public class LimboResultSet {

    private final LimboStatement statement;

    // Whether the result set does not have any rows.
    private boolean isEmptyResultSet = false;
    // If the result set is open. Doesn't mean it has results.
    private boolean open = false;
    // Maximum number of rows as set by the statement
    private long maxRows;
    // number of current row, starts at 1 (0 is used to represent loading data)
    private int row = 0;
    private boolean pastLastRow = false;

    @Nullable
    private LimboStepResult lastStepResult;

    public static LimboResultSet of(LimboStatement statement) {
        return new LimboResultSet(statement);
    }

    private LimboResultSet(LimboStatement statement) {
        this.open = true;
        this.statement = statement;
    }

    /**
     * Moves the cursor forward one row from its current position. A {@link LimboResultSet} cursor is initially positioned
     * before the first fow; the first call to the method <code>next</code> makes the first row the current row; the second call
     * makes the second row the current row, and so on.
     * When a call to the <code>next</code> method returns <code>false</code>, the cursor is positioned after the last row.
     * <p>
     * Note that limbo only supports <code>ResultSet.TYPE_FORWARD_ONLY</code>, which means that the cursor can only move forward.
     */
    public boolean next() throws SQLException {
        if (!open || isEmptyResultSet || pastLastRow) {
            return false; // completed ResultSet
        }

        if (maxRows != 0 && row == maxRows) {
            return false;
        }

        lastStepResult = this.statement.step();
        System.out.println(lastStepResult);
        pastLastRow = lastStepResult == null || lastStepResult.isDone();
        return !pastLastRow;
    }

    /**
     * Checks whether the last step result has returned row result.
     */
    public boolean hasLastStepReturnedRow() {
        return lastStepResult != null && lastStepResult.isRow();
    }

    /**
     * Checks the status of the result set.
     *
     * @return true if it's ready to iterate over the result set; false otherwise.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @throws SQLException if not {@link #open}
     */
    public void checkOpen() throws SQLException {
        if (!open) {
            throw new SQLException("ResultSet closed");
        }
    }

    @Override
    public String toString() {
        return "LimboResultSet{" +
               "statement=" + statement +
               ", isEmptyResultSet=" + isEmptyResultSet +
               ", open=" + open +
               ", maxRows=" + maxRows +
               ", row=" + row +
               ", pastLastRow=" + pastLastRow +
               ", lastResult=" + lastStepResult +
               '}';
    }
}
