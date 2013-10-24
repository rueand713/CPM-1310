//
//  SQLiteManager.h
//  CloudContacts
//
//  Created by Rueben Anderson on 10/15/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

@interface SQLiteManager : NSObject
{
    NSMutableString *DB_PATH;
    NSMutableString *DB_NAME;
    NSMutableString *TABLE_NAME;
    
    sqlite3 *DB_CONTEXT;
}

-(void)setDatabase:(NSString *)name;
-(void)setTable:(NSString *)name;
-(void)setupObject;
-(int)openDatabase;
-(int)closeDatabase;
-(NSDictionary *)createTable:(NSString *)uniqueColumnsID columnNames:(NSDictionary *)columnNames;
-(NSDictionary *)execute:(const char *)statement type:(int)statementType;
-(NSDictionary *)update:(NSDictionary *)columnNames where:(NSString *)where;
-(NSDictionary *)insert:(NSDictionary *)columnNames where:(NSString *)where;
-(NSDictionary *)deleteRow:(NSDictionary *)columnNames;
-(id)initWithDetails:(NSString *)databaseName table:(NSString *)tableName;
-(NSDictionary *)select:(NSArray *)query where:(NSString *)where;

@end
