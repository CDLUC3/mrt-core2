# Yaml File - SSM/ENV Resolover

The Merritt System has adopted AWS SSM for managing sensitive configuration properties and for managing dynamic (changeable) configuration properties.

For properties that are neither sensitive or dynamic, environment variables will be utitlized.

This library will take a LinkedHashMap that has been initialized from a Yaml file and resolve property keys by accessing SSM parameters and/or environment variables.

- [YamlParser](src/main/java/org/cdlib/mrt/tools/YamlParser) - Helper class that loads a LinkedHashMap from a Yaml file and provides access to the SSM Resolver
- [UC3ConfigResolver](src/main/java/org/cdlib/mrt/tools/UC3ConfigResolver) - Interface defining how hash map values will be resoved from SSM or environment variables
- [DefaultConfigResolver](src/main/java/org/cdlib/mrt/tools/SSMConfigResolver) - Default implementation of a hash map value resolver
- [SSMConfigResolver](src/main/java/org/cdlib/mrt/tools/SSMConfigResolver) - Resolves hash map values with an SSM lookup 
- [MockConfigResolver](src/main/java/org/cdlib/mrt/tools/SSMConfigResolver) - Simulates a resolver performaing an SSM lookup without iniitializing an AWS SSM client
- [YamlParserTest](src/test/java/org/cdlib/mrt/tools/YamlParser) - Unit test driver for the configuration property resolver
## Original System Configuration File

_The following example file illustrates how Merritt is using the SSM Parameter Resolver_

```
production:
  user: username
  password: secret_production_password
  debug-level: error
  hostname: my-prod-hostname

stage:
  user: username
  password: secret_stage_password
  debug-level: warning
  hostname: my-stage-hostname

local:
  user: username
  password: password
  debug-level: info
  hostname: localhost
```

## Step 1. Migrate secrets to SSM (`aws ssm put-parameter`)

```
/system/prod/app/db-password = secret_production_password
/system/stage/app/db-password = secret_stage_password
```

Resulting in the following

```
production:
  user: username
  password: {!SSM: app/db-password} 
  debug-level: error
  hostname: my-prod-hostname

stage:
  user: username
  password: {!SSM: app/db-password} 
  debug-level: warning
  hostname: my-stage-hostname

local:
  user: username
  password: password
  debug-level: info
  hostname: localhost
```

## Step 2. Migrate Dynamic Properties to SSM

_Run `aws ssm put-parameter` to change the debug level.  Note: the application must implement a mechanism to reload configuration on demand in order to use dynamic properties._

```
production:
  user: username
  password: {!SSM: app/db-password} 
  debug-level: {!SSM: app/debug-level !DEFAULT: error} 
  hostname: my-prod-hostname

stage:
  user: username
  password: {!SSM: app/db-password} 
  debug-level: {!SSM: app/debug-level !DEFAULT: warning}
  hostname: my-stage-hostname

local:
  user: username
  password: password
  debug-level: info
  hostname: localhost
```

## Step 3. Migrate non-secret, static values to ENV variables

_Use SSM where it provides benefit. Otherwise, ENV variables are a simpler, more portable choice._

```
production:
  user: username
  password: {!SSM: app/db-password} 
  debug-level: {!SSM: app/debug-level !DEFAULT: error} 
  hostname: {!ENV: HOSTNAME}

stage:
  user: username
  password: {!SSM: app/db-password} 
  debug-level: {!SSM: app/debug-level !DEFAULT: warning}
  hostname: {!ENV: HOSTNAME}

local:
  user: username
  password: {!ENV: DB_PASSWORD !DEFAULT: password}
  debug-level: {!ENV: DEBUG_LEVEL !DEFAULT: info}
  hostname: {!ENV: HOSTNAME !DEFAULT: localhost}
```

## Step 4. Yaml Consolidation (optional)

_It is now possible to utilize the same lookup keys for both production and stage_

```
default: &default
  user: username
  password: {!SSM: app/db-password} 
  debug-level: {!SSM: app/debug-level !DEFAULT: error} 
  hostname: {!ENV: HOSTNAME}

stage:
  <<: *default

production:
  <<: *default

local:
  user: username
  password: {!ENV: DB_PASSWORD !DEFAULT: password}
  debug-level: {!ENV: DEBUG_LEVEL !DEFAULT: info}
  hostname: {!ENV: HOSTNAME !DEFAULT: localhost}
```


## Resolving the Configuration

Run in production

```
export SSM_ROOT_PATH=/system/prod/
export HOSTNAME=my-prod-hostname
```

Run in stage

```
export SSM_ROOT_PATH=/system/prod/
export HOSTNAME=my-stage-hostname

```

Run locally -- bypass SSM resolution when not running on AWS

```
export SSM_SKIP_RESOLUTION=Y
export HOSTNAME=localhost
export DB_PASSWORD=password
export DEBUG_LEVEL=info
```

## Example Usage

```
  String filename = "input.yml"
  SSMConfigResolver ssmResolver = new SSMConfigResolver();
  YamlParser yamlParser = new YamlParser(ssmResolver);
  yamlParser.parse(filename);
  LinkedHashMap<String, Object> config = yamlParser.resolveValues();
  config.get("key")
```

## See Also

- https://github.com/CDLUC3/uc3-aws-cli (Bash Implementation)
- https://github.com/CDLUC3/uc3-ssm (Ruby Implementation)
