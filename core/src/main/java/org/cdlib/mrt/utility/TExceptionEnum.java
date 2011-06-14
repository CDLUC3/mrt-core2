/*
Copyright (c) 2005-2010, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
**********************************************************/
package org.cdlib.mrt.utility;

/**
 * Enumeration of exception types with description and http status value
 * @author dloy
 */
public enum TExceptionEnum
{
    INVALID_ARCHITECTURE("Required architectural data element is missing or invalid", 500),

    REQUEST_INVALID("User/request supplied required parm is missing or invalid", 400),

    REQUEST_ELEMENT_UNSUPPORTED("User/request supplied parm type is not supported", 501),

    USER_NOT_AUTHORIZED("User not authorized", 401),

    USER_NOT_AUTHENTICATED("User not authenticated", 401),

    CONFIGURATION_INVALID("Program required property is missing or invalid", 500),

    INVALID_OR_MISSING_PARM("Program required parm is missing or invalid", 500),

    INVALID_CONFIGURATION("Configuration is invalid", 500),

    GENERAL_EXCEPTION("Unexpected Programmic exception", 500),

    EXTERNAL_SERVICE_UNAVAILABLE("Unavailable remote service", 503),

    EXTERNAL_SERVICE_FAILS("Failed request to external dependent service", 503),

    INVALID_DATA_FORMAT("Generic data being processed is invalidly formatted", 500),

    LOCKING_ERROR("Error while attempting lock", 503),

    FIXITY_CHECK_FAILS("Fixity comparison between saved file and returned file fails", 500),

    CONCURRENT_UPDATE("Concurrent update of same Version", 500),

    SQL_EXCEPTION("SQL exception", 500),

    REQUEST_ITEM_EXISTS("Request fails because element already exists", 404),

    REQUESTED_ITEM_NOT_FOUND("Request contains element that cannot be located", 404),

    UNIMPLEMENTED_CODE("Unimplemented Code", 501),

    UNSUPPORTED_FORMAT_TYPE("Requested format not supported", 501);

    /*
    FILE_CREATION_ERROR("Error during creation of critical file"),
    ATTEMPT_TO_ADD_INCONSISTENT_OBJECT("Attempt to Add Inconsistent Object"),
    INVALID_COLLECTION_ID("Invalid Collection ID"),
    INVALID_SERVICE_ID("Invalid Service ID"),
    INVALID_ACCESS_GROUP_ID("Invalid Access Group ID"),
    INVALID_USER_ID("Invalid User ID"),
    OBJECT_DOES_NOT_EXIST("Object Does Not Exist"),
    IDENTIFIER_CONVERSION_ERROR("Identifier Conversion Error"),
    COMPONENT_DOES_NOT_EXIST("Component Does Not Exist"),
    CLIENT_GET_COMPONENTS_ERROR("Client Get Components Error"),
    NO_VERSION_FOUND("version data must exist for this operation"),
    SQL_EXCEPTION("SQL Exception"),
    INSTANTIATION_ERROR("Instantiation Error"),
    INITIALIZE_ERROR("Error during object Initialize"),
    CONCURRENT_UPDATE("Concurrent update of same Version"),
    STORAGEMANAGER_WRITE_ERROR("Storage Manager Write Error"),
    STORAGEMANAGER_READ_ERROR("Storage Manager Read Error"),
    STORAGEMANAGER_REMOVE_ERROR("Storage Manager Remove Error"),
    STORAGEMANAGER_CONNECTION_ERROR("Unable to Connect to the Storage Manager"),
    FILEMANAGER_CREATE_TEMP_ERROR("Could Not Create Temporary File"),
    USER_NOT_AUTHORIZED("User not authorized"),
    USER_NOT_AUTHENTICATED("User not authenticated"),
    INGEST_PARSE_ERROR("Ingest Parse Error"),
    QUERY_PARSE_ERROR("Query Parse Error"),
    INGEST_VALIDATION_ERROR("Ingest Validation Error"),
    INGEST_INGESTOR_ERROR("Ingest Ingestor Error"),
    METASEARCH_MISC_ERROR("General Metasearch Exception"),
    METASEARCH_GLOBAL_ERROR("Metasearch Timeout or Bad XML Exception"),
    METASEARCH_RESPONSE_ERROR("Invalid Metasearch XML Response"),
    METASEARCH_LOCAL_ERROR("Metasearch XML Request Exception"),
    METASEARCH_TIMEOUT("Metasearch Session Timeout"),
    TEXT_GENERAL_EXCEPTION("General Text Indexing Exception"),
    TEXT_MISSING_INPUT("Text For Indexing is Missing"),
    TEXT_INVALID_MAP("Invalid Value in Text Indexing Map"),
    ENRICHMENT_GENERAL_EXCEPTION("General Enrichment Indexing Exception"),
    CLIENT_MISC_ERROR("Client Misc Error"),
    CLIENT_REQUESTER_NOT_FOUND("Client Side Requester Not Found"),
    CLIENT_REQUESTER_MISC_ERROR("Client Requester Misc Error"),
    CLIENT_REQUESTER_PARAMETER_MISC_ERROR("Client Side Requester Parameter Misc Error"),
    CLIENT_RECEIVED_BADLY_FORMED_SOAP("Client Received Badly Formed SOAP"),
    CLIENT_RECEIVED_SOAP_FAULT("Client Received SOAP Fault"),
    CLIENT_RECEIVED_SERVER_TIMEOUT("Client Received Server Timeout"),
    UNIMPLEMENTED_CODE("Unimplemented Code"),
    REQUIRED_PARM_MISSING("Required Parameter Missing"),
    REQUIRED_PROPERTY_MISSING("Required Property Missing"),
    REQUIRED_COMPONENT_MISSING("Required Components) Missing"),
    REQUIRED_FILE_MISSING("Required File Missing"),
    SOAP_EXCEPTION("SOAP Exception"),
    IDMANAGER_MINT_ERROR("ID Manager Could Not Mint an Identifier"),
    TRANSACTION_LOG_ERROR("Error logging transaction"),
    INVALID_URL("Invalid URL"),
    HTTP_ERROR("Error in HTTP Connection"),
    INVALID_DATA_FORMAT("Generic data being processed is invalidly formatted"),
    GENERAL_STORAGE_EXCEPTION("General error during storage processing"),
    LOCKING_ERROR("Error while attempting lock"),
    CHARACTER_ERROR("Character Error"),
    FIXITY_CHECK_FAILS("Fixity comparison between saved file and returned file fails"),
    REQUESTED_ELEMENT_NOT_FOUND("Request contains element that cannot be located"),
    REQUIRED_REQUEST_PARM_MISSING("Required request parameter is missing"),
    REQUEST_PARM_INVALID("Request parameter is invalid"),
    UNSUPPORTED_FORMAT_TYPE("Requested format not supported");
    */

    protected final String description;
    protected final int httpResponse;

    TExceptionEnum(String description, int httpResponse) {
        this.description = description;
        this.httpResponse = httpResponse;
    }
    public String getDescription()
    {
        return description;
    }
    public int getHttpResponse()
    {
        return httpResponse;
    }
}