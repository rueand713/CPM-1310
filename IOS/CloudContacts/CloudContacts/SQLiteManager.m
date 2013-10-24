//
//  SQLiteManager.m
//  CloudContacts
//
//  Created by Rueben Anderson on 10/15/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import "SQLiteManager.h"

typedef enum {
    SQL_CREATE_TABLE = 0,
    SQL_INSERT,
    SQL_SELECT,
    SQL_DELETE,
    SQL_UPDATE
    
} statementTypes;

@implementation SQLiteManager

// error proofing initialization by overwriting standard init
- (id)init
{
    self = [super init];
    
    // verify that self is valid
    if (self)
    {
        // call the method to setup the object properties
        [self setupObject];
        
        NSLog(@"Using this function requires the manual method calls for setting table name, database name, and database creation");
    }
    
    return self;
}

// default initializing method
- (id)initWithDetails:(NSString *)databaseName table:(NSString *)tableName
{
    self = [super init];
    
    if (self)
    {
        // call the method to setup the object properties
        [self setupObject];
        
        // call the methods to set the database and table names
        [self setTable:tableName];
        [self setDatabase:databaseName];
        
        // calls the method that creates/opens the sqlite database
        int result = [self openDatabase];
        
        // log the results for debugging
        if (result == SQLITE_OK)
        {
            NSLog(@"Database open/creation was successful");
        }
    }
    
    return self;
}

// method for setting the database name
-(void)setDatabase:(NSString *)name
{
    // set the name of the database
    DB_NAME = (NSMutableString *) name;
}

// method for setting the table name
-(void)setTable:(NSString *)name
{
    // set the table name
    TABLE_NAME = (NSMutableString *) name;
}

// this method is called during the initializing of the object
-(void)setupObject
{
    // get an array of available directories
    NSArray *deviceDirectories = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true);
    
    // verify that the directories array is valid
    if (deviceDirectories != nil)
    {
        // retrieve the first directory location
        DB_PATH = (NSMutableString *) [deviceDirectories objectAtIndex:0];
    }
}

// method for opening/creating an sqlite database
-(int)openDatabase
{
    // create a new string of the concatenated path and name
    NSString *fullPath = [DB_PATH stringByAppendingFormat:@"/%@", DB_NAME];
    
    NSLog(@"Path: %@", fullPath);
    
    // create a filename char object of the fullpath string
    const char *fileName = [fullPath UTF8String];
    
    // attempt to open/create the database and capture the result value
    int success = sqlite3_open(fileName, &DB_CONTEXT);
    
    // return that value
    return success;
}

// method for closing the sqlite database
-(int)closeDatabase
{
    // attempt to close the database and capture the result value
    int success = sqlite3_close(DB_CONTEXT);
    
    // return that value
    return success;
}

-(NSDictionary *)createTable:(NSString *)uniqueColumnsID columnNames:(NSDictionary *)columnNames
{
    // create an array of the dictionary keys (table column names)
    NSArray *keys = [columnNames allKeys];
    
    // create a mutable string for creating the column name and details sql statement
    NSMutableString *columnData;
    
    // the results object to be returned from the method
    NSDictionary *results = [NSDictionary alloc];
    
    // iterate over the keys and concatenate the columnData to form the sql creation statement
    for (int i = 0; i < [keys count]; i++)
    {
        // create a string to hold the columnDetails (dictionary values)
        NSString *columnDetails = [columnNames objectForKey:[keys objectAtIndex:i]];
        
        // create a string to hold the current dictionary key
        NSString *columnName = [keys objectAtIndex:i];
        
        // create the current sql statement from the dictionary using the columnDetails and columnName strings
        NSString *columnStatement = [NSString stringWithFormat:@"%@ %@", columnName, columnDetails];
        
        // check if this is the first loop or not
        if (i == 0)
        {
            // since this is the first loop allocate and initialize the string with the columnstatment
            columnData = [[NSMutableString alloc] initWithString:columnStatement];
        }
        else
        {
            // not the first loop, create a new string appending the current columnStatement string
            columnData = [NSMutableString stringWithFormat:@"%@, %@", columnData, columnStatement];
        }
    }
    
    // create the creation statement object
    NSString *tableCreationString;
    
    // create the creation statement with multiple supplied columns
    if (columnNames != nil)
    {
        tableCreationString = [[NSString alloc] initWithFormat:@"CREATE TABLE IF NOT EXISTS %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@)", TABLE_NAME, uniqueColumnsID, columnData];
    }
    else
    {
        // create the creation statement with only the unique id column
        tableCreationString = [[NSString alloc] initWithFormat:@"CREATE TABLE IF NOT EXISTS %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT)", TABLE_NAME, uniqueColumnsID];
    }
    
    NSLog(@"%@", tableCreationString);
    
    // verify that the statement string is valid
    if (tableCreationString != nil)
    {
        // create the char object from the creation string
        const char *tableCreationStatement = [tableCreationString UTF8String];
        
        // verify that the tableCreationStatement char object is valid
        if (tableCreationStatement != nil)
        {
            // pass the sql statement into the execute function for sqlite execution
            [self execute:tableCreationStatement type:SQL_CREATE_TABLE];
        }
    }
    
    return results;
}

-(NSDictionary *)insert:(NSDictionary *)columnNames where:(NSString *)where
{
    // create an array of the dictionary keys (table column names)
    NSArray *keys = [columnNames allKeys];
    
    // create a mutable string for creating the column name sql statement
    NSMutableString *columnData;
    
    // create a mutable string for creating the values details sql statement
    NSMutableString *valuesData;
    
    // the results object to be returned from the method
    NSDictionary *results = [NSDictionary alloc];
    
    // iterate over the length of the keys array
    for (int i = 0; i < [keys count]; i++)
    {
        // check if this loop is the first or not
        if (i == 0)
        {
            // first loop - allocate and init the strings with the first column and value data
            columnData = [[NSMutableString alloc] initWithString:[keys objectAtIndex:i]];
            valuesData = [[NSMutableString alloc] initWithString:[columnNames objectForKey:[keys objectAtIndex:i]]];
        }
        else
        {
            // not first loop - create new temporary strings to hold the data at the current array index
            NSString *newColumn = [NSString stringWithString:[keys objectAtIndex:i]];
            columnData = [NSMutableString stringWithFormat:@"%@, %@", columnData, newColumn];
            
            // append the new strings to the existing strings
            NSString *newValue = [NSString stringWithString:[columnNames objectForKey:[keys objectAtIndex:i]]];
            valuesData = [NSMutableString stringWithFormat:@"%@, %@", valuesData, newValue];
        }
    }
    
    // create the insert sql statement string
    NSString *insertStatement = [[NSString alloc] initWithFormat:@"INSERT INTO %@ (%@) VALUES (%@)", TABLE_NAME, columnData, valuesData];
    
    if (insertStatement != nil)
    {
        // check if there is a where statement provided
        if (where != nil)
        {
            // append the where statement to the sql statement string
            insertStatement = [NSString stringWithFormat:@"%@ WHERE %@", insertStatement, where];
        }
        
        // create a sqlite const char object from the insert string
        const char *dataInsertionStatement = [insertStatement UTF8String];
        
        // verify that the sqlite char statement was created properly
        if (dataInsertionStatement != nil)
        {
            // execute the statement
            [self execute:dataInsertionStatement type:SQL_INSERT];
        }
    }
    
     NSLog(@"%@", insertStatement);
    
    return results;
}

-(NSDictionary *)deleteRow:(NSDictionary *)columnNames
{
    // create an array of the dictionary keys (table column names)
    NSArray *keys = [columnNames allKeys];
    
    // create a mutable string for creating the values details sql statement
    NSMutableString *value;
    
    // the results object to be returned from the method
    NSDictionary *results = [NSDictionary alloc];

    // iterate over the length of the keys array
    for (int i = 0; i < [keys count]; i++)
    {
        // create a mutable string for creating the column name sql statement
        NSMutableString *column = [[NSMutableString alloc] initWithString:[keys objectAtIndex:i]];
        
        // create a string of the column value
        NSString *colValue = [columnNames objectForKey:[keys objectAtIndex:i]];

        // check if this loop is the first or not
        if (i == 0)
        {
            // first loop - allocate and init the strings with the first column and value data
            value = [[NSMutableString alloc] initWithFormat:@"%@=%@", column, colValue];
        }
        else
        {
            // not first loop - create new temporary strings to hold the data at the current array index
            NSString *newValue = [[NSString alloc] initWithFormat:@"%@=%@", column, colValue];
            
            // append the new strings to the existing strings
            value = [NSMutableString stringWithFormat:@"%@ AND %@", value, newValue];
            
        }
    }
    
    // create the insert sql statement string
    NSString *deleteStatement = [[NSString alloc] initWithFormat:@"DELETE FROM %@ WHERE %@", TABLE_NAME, value];
    
    NSLog(@"%@", deleteStatement);
    
    if (deleteStatement != nil)
    {
        // create a sqlite const char object from the insert string
        const char *dataDeleteStatement = [deleteStatement UTF8String];
        
        // verify that the sqlite char statement was created properly
        if (dataDeleteStatement != nil)
        {
            // execute the statement
            [self execute:dataDeleteStatement type:SQL_DELETE];
        }
    }
    
    return results;
}

-(NSDictionary *)update:(NSDictionary *)columnNames where:(NSString *)where
{
    // create an array of the dictionary keys (table column names)
    NSArray *keys = [columnNames allKeys];
    
    // create a mutable string for creating the values details sql statement
    NSMutableString *value;
    
    // the results object to be returned from the method
    NSDictionary *results = [NSDictionary alloc];
    
    // iterate over the length of the keys array
    for (int i = 0; i < [keys count]; i++)
    {
        // create a mutable string for creating the column name sql statement
        NSMutableString *column = [[NSMutableString alloc] initWithString:[keys objectAtIndex:i]];
        
        // create a string of the column value
        NSString *colValue = [columnNames objectForKey:[keys objectAtIndex:i]];
        
        // check if this loop is the first or not
        if (i == 0)
        {
            // first loop - allocate and init the strings with the first column and value data
            value = [[NSMutableString alloc] initWithFormat:@"%@=%@", column, colValue];
        }
        else
        {
            // not first loop - create new temporary strings to hold the data at the current array index
            NSString *newValue = [[NSString alloc] initWithFormat:@"%@=%@", column, colValue];
            
            // append the new strings to the existing strings
            value = [NSMutableString stringWithFormat:@"%@, %@", value, newValue];
            
        }
    }
    
    // create the insert sql statement string
    NSString *updateStatement = [[NSString alloc] initWithFormat:@"UPDATE %@ SET %@", TABLE_NAME, value];
    
    if (updateStatement != nil)
    {
        // check if there is a where statement provided
        if (where != nil)
        {
            // append the where statement to the sql statement string
            updateStatement = [NSString stringWithFormat:@"%@ WHERE %@", updateStatement, where];
        }
        
        // create a sqlite const char object from the insert string
        const char *dataUpdateStatement = [updateStatement UTF8String];
        
        // verify that the sqlite char statement was created properly
        if (dataUpdateStatement != nil)
        {
            // execute the statement
            [self execute:dataUpdateStatement type:SQL_UPDATE];
        }
    }
    
    NSLog(@"%@", updateStatement);

    return results;
}

-(NSDictionary *)select:(NSArray *)query where:(NSString *)where
{
    // the string to hold the select query data
    NSString *select;
    
    // the results object to be returned from the select method
    NSDictionary *results = [NSDictionary alloc];
    
    // iterate over the query array to create the selection data string
    for (int i = 0; i < [query count]; i++)
    {
        if (i == 0)
        {
            // allocate and initialize the string for the first loop
            select = [[NSString alloc] initWithString:[query objectAtIndex:i]];
        }
        else
        {
            // create a new string with the next selection query appended to it
            select = [NSString stringWithFormat:@"%@, %@", select, [query objectAtIndex:i]];
        }
    }
    
    // create the complete selection statement string
    NSString *selectStatement = [[NSString alloc] initWithFormat:@"SELECT %@ FROM %@", select, TABLE_NAME];
    
    if (selectStatement != nil)
    {
        // check if there is a where statement provided
        if (where != nil)
        {
            selectStatement = [NSString stringWithFormat:@"%@ WHERE %@", selectStatement, where];
        }
        
        // create a sqlite const char object from the insert string
        const char *dataSelectStatement = [selectStatement UTF8String];
        
        // verify that the sqlite char statement was created properly
        if (dataSelectStatement != nil)
        {
            // execute the statement
            results = [self execute:dataSelectStatement type:SQL_SELECT];
        }
    }
    
    NSLog(@"%@", selectStatement);
    
    // returns the dictionary returned from the execution method
    return results;
}

// shorthand method for executing simple sql statement
// for selection statements a NSDictionary should be supplied for returning the selected data otherwise pass nil
-(NSDictionary *)execute:(const char *)statement type:(int)statementType
{
    // error char pointer
    char *err;
    
    // integer for the result code
    int result = 0;
    
    // create a dictionary for holding the returned data
     NSMutableDictionary *resultSet = [[NSMutableDictionary alloc] init];
    
    // check that the statement is required to be compiled or not
    if (statementType == SQL_CREATE_TABLE)
    {
        // execute the sqlite statement, capture the result and any resulting error
        result = sqlite3_exec(DB_CONTEXT, statement, NULL, NULL, &err);
        
        NSLog(@"Table Creation Result: %i", result);
        
        if (err != NULL)
        {
            NSLog(@"%@", [NSString stringWithUTF8String:err]);
        }
    }
    else
    {
        // create a new compiled sqlite object
        sqlite3_stmt *compileStatement;
        
        // verify that the sqlite statement was compiled successfully
        if (sqlite3_prepare_v2(DB_CONTEXT, statement, -1, &compileStatement, NULL) == SQLITE_OK)
        {
            // determine the type of statement is being executed
            if (statementType == SQL_INSERT || statementType == SQL_UPDATE || statementType == SQL_DELETE)
            {
                // execute and check that the step method has finished
                if (sqlite3_step(compileStatement) == SQLITE_DONE)
                {
                    // finalize and commit the sqlite statement compilation and capture the result code
                    result = sqlite3_finalize(compileStatement);
                    
                    NSLog(@"CRUD operation Result: %i", result);
                }
            }
            else if (statementType == SQL_SELECT)
            {
                // integer value for representing the number of columns in the data
                int numColumns = sqlite3_column_count(compileStatement);
                
                // integer value for tracking the current row
                int row = 0;
                
                // loop through the sqlite data for each row
                while (sqlite3_step(compileStatement) == SQLITE_ROW)
                {
                    // create a array to hold the row data
                    NSMutableDictionary *rowData = [[NSMutableDictionary alloc] initWithCapacity:numColumns];
                    
                    // create a string for setting a key that corresponds to the row data it holds in the dictionary
                    NSString *rowKey = [NSString stringWithFormat:@"row%i", row];
                    
                    // iterate over the rows returned extracting the column data
                    for (int i = 0; i < numColumns; i++)
                    {
                        const char *columnText = (const char *) sqlite3_column_text(compileStatement, i);
                        const char *columnValue = sqlite3_column_name(compileStatement, i);
                        
                        if (columnText != NULL)
                        {
                            // create a string for the column data at the current index for the row
                            NSString *value = [NSString stringWithUTF8String: columnText];
                            NSString *column = [NSString stringWithFormat:@"column%i", i];
                            
                            if (columnValue != NULL)
                            {
                                // create a string from the column data value at the current index for the row
                                column = [NSString stringWithUTF8String: columnValue];
                            }
                            
                            // add the column data to the array
                            [rowData setObject:value forKey:column];
                        }
                    }
                    
                    // add the array of row data to the dictionary
                    [resultSet setObject:rowData forKey:rowKey];
                    
                    // increment the row counter
                    row++;
                }
                
                // finalize and commit the sqlite statement compilation and capture the result code
                result = sqlite3_finalize(compileStatement);
                
                NSLog(@"Select Result: %i", result);
            }
        }
    }
    
    // returns the result code of the exuction and any data returned from a select statement
    return @{
             @"result": [NSNumber numberWithInt:result],
             @"data": resultSet
             };
}

@end