# Guice Config

Read and inject values from configuration files into Guice managed classes.

# Source loading

This library uses the Typesafe Config library to support most file formats:

- HOCON conf files
- JSON
- Property files

3rd party formats can also be supported as long as they can be parsed into a Map. 
YAML files are supported via SnakeYAML and this feature.

# Source precedence

By default, the following sources are parsed and take precedence over each other in the following order

- System properties
- Environment variables
- Application override file
- Main Application file
- All Typesafe config Reference files (in case you want create libraries with default configuration settings)

You can use **ConfigurationModule** to bind configuration values using the default precedence rules.
You can also use **ConfigBinder** directly, providing a Typesafe Config instance.

# Limitations

- Lists any type of numbers use **Number** type class

# Examples

Please have a look at **ConfigurationModuleTest** for how a typical use works.



