/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;

/**
 * Container class, used for tallying Directory file statistics
 * @author dloy
 */
public class DirectoryStats
{
    public int fileCnt = 0;
    public long fileSize = 0;

    /**
     * get number of files
     * @return number files
     */
    public int getFileCnt() {
        return fileCnt;
    }

    /**
     * set number of files
     * @param fileCnt number of files to set
     */
    public void setFileCnt(int fileCnt) {
        this.fileCnt = fileCnt;
    }

    /**
     * get accumulated size of files
     * @return accumulated size of files
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * set accumulated size of files
     * @param fileSize accumulated size of files
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * copy constructor
     * @return copied DirectoryStats
     */
    public DirectoryStats copy()
    {
        DirectoryStats copyStats = new DirectoryStats();
        copyStats.fileCnt = this.fileCnt;
        copyStats.fileSize = this.fileSize;
        return copyStats;
    }

    /**
     * Add directory tallies from other DirectoryStats to this stats
     * @param stats stats to add to this
     */
    public void add(DirectoryStats stats)
    {
        this.fileCnt += stats.fileCnt;
        this.fileSize += stats.fileSize;
    }

    /**
     * Subtract other DirectoryStats from this status
     * @param stats stats to subtract from this
     */
    public void subtract(DirectoryStats stats)
    {
        this.fileCnt -= stats.fileCnt;
        this.fileSize -= stats.fileSize;
    }
}
