# ABAP SQL Beautifier
 
 This plugin adds a quick fix to your ADT environment for formatting your ABAP SQL statement. You can customize it in the preferences:

- Align operators
- Standardize operators (e.g., convert all 'EQ' to '=')
- Add spaces to each line
- Additional spaces in condition lines
- Order of keywords (please use it wisely)
- Combine SELECT with FROM/INTO until a specific character limit
- Combine FROM with JOINs until a specific character limit


Here are some examples with different settings:

![example1](example1.gif)

![example2](example2.gif)



Don't forget to install the plugin's backend to your SAP system: https://github.com/lukas-mb/ABAP-SQL-Beautifier-Backend

Without the backend, formatting will still work, but the entire statement will be in uppercase.

Please note that it's disabled in some cases. These will be implemented in the future. If you encounter any bugs, please report them. Have fun!

Planned for V1:
- Convert SQL statement to new syntax
- Add escaping to host variables
- Add commas between fields
- WHEN syntax
- SELECT in WHERE

