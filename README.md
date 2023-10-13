# ABAP SQL Beautifier
 
 This plugin adds a quick fix to your ADT environment for formatting your ABAP SQL statement. You can customize it in the preferences:
- Align operators
- Standardize operators (e.g., convert all 'EQ' to '=')
- Add spaces to each line
- Additional spaces in condition lines
- Order of keywords (please use it wisely)
- Combine SELECT with FROM/INTO until a certain character limit
- Combine FROM with JOINs until a certain character limit


![example1](example1.gif)

![example2](example2.gif)

<br>

Don't forget to install the plugin's backend to your SAP system: https://github.com/lukas-mb/ABAP-SQL-Beautifier-Backend 
<br> 
Without the backend, formatting will still work, but the entire statement will be in uppercase.

Still in Beta. If you encounter any bugs, feel free to report them.

Please note that it's disabled in some cases. These will be implemented in the future. 



Planned for V1:
- Convert SQL statement to new syntax
- Add escaping to host variables
- Add commas between fields
- WHEN syntax
- SELECT in WHERE


<br>
Direct installation: 
https://lukas-mb.github.io/ABAP-SQL-Beautifier-Update/

<br>

Installation via Eclipse Marketplace:

<a href="https://marketplace.eclipse.org/marketplace-client-intro?mpc_install=5690679" class="drag" title="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client"><img style="width:80px;" typeof="foaf:Image" class="img-responsive" src="https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.svg" alt="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client" /></a>


