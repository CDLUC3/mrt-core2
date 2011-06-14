package org.cdlib.mrt.core;

import org.cdlib.mrt.core.ManifestRowInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;

/**
 * Factory class for ManifestRow
 * @author dloy
 */
public abstract class ManifestRowAbs
{
    protected static final String NAME = "ManifestRowAbs";
    protected static final String MESSAGE = NAME + ": ";

    /**
     * Manifest types
     * dflat - as saved in a dflat file
     * dflatpost - as sent to the storage service for dflat generation
     */
    public static enum ManifestType {
        object, add, ingest, batch;
    }

    protected LoggerInf m_logger = null;



    /**
     * Constructor
     * @param logger process logger
     */
    protected ManifestRowAbs (
            LoggerInf logger)
    {
        this.m_logger = logger;
    }

    /**
     * Based on ManifestRow type return requested ManifestRow handler
     * @param type manifest row type
     * @param logger process logger
     * @return ManifestRow handler for this manifest row type
     * @throws TException
     */
    public static ManifestRowInf getManifestRow(ManifestType type, LoggerInf logger)
            throws TException
    {
        if (type == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "Missing Parm in ObjectStoreFactory");
        }

        ManifestRowInf row = null;
        try{
            if (type == ManifestType.add) {
                row = new ManifestRowAdd(logger);
                return row;
            }
            if (type == ManifestType.object) {
                row = new ManifestRowObject(logger);
                return row;
            }
            if (type == ManifestType.ingest) {
                row = new ManifestRowIngest(logger);
                return row;
            }
            if (type == ManifestType.batch) {
                row = new ManifestRowBatch(logger);
                return row;
            }
            /*
            if (type.equals("dflatmanifest")) {
                row = new ManifestRow_dflatmanifest(logger);
                return row;
            }
             */
            throw new TException.INVALID_OR_MISSING_PARM(
                    "ObjectManifestRow type not found");

        } catch (Exception ex) {
            ex.printStackTrace();
                throw new TException.INVALID_OR_MISSING_PARM(
                    "Unable to instantiate ObjectManifestRow: Exception" + ex);
        }
    }
    /**
     * Based on ManifestRow type return requested ManifestRow handler
     * @param type manifest row type
     * @param logger process logger
     * @return ManifestRow handler for this manifest row type
     * @throws TException
     */
    public static ManifestRowInf getManifestRow(
            ManifestType type,
            String outputProfile,
            LoggerInf logger)
            throws TException
    {
        if (type == null) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "Missing Parm in ObjectStoreFactory");
        }

        ManifestRowInf row = null;
        try{
            if (type == ManifestType.batch) {
                row = new ManifestRowBatch(outputProfile, logger);
                return row;
            }
            /*
            if (type.equals("dflatmanifest")) {
                row = new ManifestRow_dflatmanifest(logger);
                return row;
            }
             */
            throw new TException.INVALID_OR_MISSING_PARM(
                    "ObjectManifestRow type not found");

        } catch (Exception ex) {
            ex.printStackTrace();
                throw new TException.INVALID_OR_MISSING_PARM(
                    "Unable to instantiate ObjectManifestRow: Exception" + ex);
        }
    }
}

