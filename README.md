# AppDynamics Monitoring Extension for use with AWS DynamoDB

## Use Case
Captures DynamoDB statistics from Amazon CloudWatch and displays them in the AppDynamics Metric Browser.

## Prerequisites
1. Please give the following permissions to the account being used to with the extension.
```
cloudwatch:ListMetrics
cloudwatch:GetMetricStatistics
```
2. Before the extension is installed, the prerequisites mentioned [here](https://community.appdynamics.com/t5/Knowledge-Base/Extensions-Prerequisites-Guide/ta-p/35213) need to be met. Please do not proceed with the extension installation if the specified prerequisites are not met.

3. The extension needs to be able to connect to AWS Cloudwatch in order to collect and send metrics. To do this, you will have to either establish a remote connection in between the extension and the product, or have an agent on the same machine running the product in order for the extension to collect and send the metrics.

## Installation

1. Run `mvn clean install` from aws-dynamodb-monitoring-extension directory
2. Copy and unzip `AWSDynamoDBMonitor-<version>.zip` from `target` directory into `<machine_agent_dir>/monitors/`
3. Edit config.yml file in AWSDynamoDBMonitor and provide the required configuration (see Configuration section)
4. Restart the Machine Agent.

Please place the extension in the `monitors` directory of your Machine Agent installation directory. Do not place the extension in the `extensions` directory of your Machine Agent installation directory.

## Configuration
In order to use the extension, you need to update the config.yml file that is present in the extension folder. The following is an explanation of the configurable fields that are present in the config.yml file.
1. If SIM is enabled, then use the following metricPrefix `metricPrefix: "Custom Metrics|AWS DynamoDB"` else configure the "COMPONENT_ID" under which the metrics need to be reported. This can be done by changing the value of <COMPONENT_ID> in `metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|AWS DynamoDB|"`.

   For example,
     ```
     metricPrefix: "Server|Component:100|Custom Metrics|AWS DynamoDB|"
     ```

More details around metric prefix can be found [here](https://community.appdynamics.com/t5/Knowledge-Base/How-do-I-troubleshoot-missing-custom-metrics-or-extensions/ta-p/28695).

2. Provide accessKey(required) and secretKey(required) of AWS account(s), also provide displayAccountName(any name that represents your account) and regions(required). If you are running this extension inside an EC2 instance which has IAM profile configured then awsAccessKey and awsSecretKey can be kept empty, extension will use IAM profile to authenticate.
   ```
   accounts:
     - awsAccessKey: "XXXXXXXX1"
       awsSecretKey: "XXXXXXXXXX1"
       displayAccountName: "TestAccount_1"
       regions: ["us-east-1","us-west-1","us-west-2"]
   
     - awsAccessKey: "XXXXXXXX2"
       awsSecretKey: "XXXXXXXXXX2"
       displayAccountName: "TestAccount_2"
       regions: ["eu-central-1","eu-west-1"]
   ```    
3. If you want to encrypt the "awsAccessKey" and "awsSecretKey" then follow the "Credentials Encryption" section and provide the encrypted values in "awsAccessKey" and "awsSecretKey". Configure "enableDecryption" of "credentialsDecryptionConfig" to true and provide the encryption key in "encryptionKey"
   For example,
   ```
   #Encryption key for Encrypted password.
   credentialsDecryptionConfig:
       enableDecryption: "true"
       encryptionKey: "XXXXXXXX"
   ```
4. To report metrics from specific tables, configure `includeTableNames` which accepts comma separated values and regex patterns. If `.*` is used, all are monitored and if empty, none are monitored.
   ```
   includeTableNames: ["blog-*", "demodb"]
   ```
5. Configure the metrics section.

     For configuring the metrics, the following properties can be used:

     |     Property      |   Default value |         Possible values         |                                              Description                                                                                                |
     | :---------------- | :-------------- | :------------------------------ | :------------------------------------------------------------------------------------------------------------- |
     | alias             | metric name     | Any string                      | The substitute name to be used in the metric browser instead of metric name.                                   |
     | statType          | "ave"           | "AVERAGE", "SUM", "MIN", "MAX"  | AWS configured values as returned by API                                                                       |
     | aggregationType   | "AVERAGE"       | "AVERAGE", "SUM", "OBSERVATION" | [Aggregation qualifier](https://docs.appdynamics.com/display/latest/Build+a+Monitoring+Extension+Using+Java)    |
     | timeRollUpType    | "AVERAGE"       | "AVERAGE", "SUM", "CURRENT"     | [Time roll-up qualifier](https://docs.appdynamics.com/display/latest/Build+a+Monitoring+Extension+Using+Java)   |
     | clusterRollUpType | "INDIVIDUAL"    | "INDIVIDUAL", "COLLECTIVE"      | [Cluster roll-up qualifier](https://docs.appdynamics.com/display/latest/Build+a+Monitoring+Extension+Using+Java)|
     | multiplier        | 1               | Any number                      | Value with which the metric needs to be multiplied.                                                            |
     | convert           | null            | Any key value map               | Set of key value pairs that indicates the value to which the metrics need to be transformed. eg: UP:0, DOWN:1  |
     | delta             | false           | true, false                     | If enabled, gives the delta values of metrics instead of actual values.                                        |

    For example,
    ```
    - name: "ConditionalCheckFailedRequests"
      alias: "ConditionalCheckFailedRequests"
      statType: "ave"
      delta: false
      multiplier: 1
      aggregationType: "AVERAGE"
      timeRollUpType: "AVERAGE"
      clusterRollUpType: "INDIVIDUAL"
    ```
    
    **All these metric properties are optional, and the default value shown in the table is applied to the metric(if a property has not been specified) by default.**

### config.yml

Please avoid using tab (\t) when editing yaml files. Please copy all the contents of the config.yml file and go to [Yaml Validator](https://jsonformatter.org/yaml-validator) . On reaching the website, paste the contents and press the “Validate YAML” button.                                                       
If you get a valid output, that means your formatting is correct and you may move on to the next step.

**Below is an example config for monitoring multiple accounts and regions:**

~~~
metricPrefix: "Server|Component:<COMPONENT_ID>|Custom Metrics|Amazon DynamoDB|"

accounts:
  - awsAccessKey: "XXXXXXXX1"
    awsSecretKey: "XXXXXXXXXX1"
    displayAccountName: "TestAccount_1"
    regions: ["us-east-1","us-west-1","us-west-2"]

  - awsAccessKey: "XXXXXXXX2"
    awsSecretKey: "XXXXXXXXXX2"
    displayAccountName: "TestAccount_2"
    regions: ["eu-central-1","eu-west-1"]

credentialsDecryptionConfig:
    enableDecryption: "false"
    encryptionKey:

proxyConfig:
    host:
    port:
    username:
    password:   
    
includeTableNames: []

cloudWatchMonitoring: "Basic"

concurrencyConfig:
  noOfAccountThreads: 3
  noOfRegionThreadsPerAccount: 3
  noOfMetricThreadsPerRegion: 3
  #Thread timeout in seconds
  threadTimeOut: 30 

metricsConfig:
    includeMetrics:
       - name: "ConditionalCheckFailedRequests"
         alias: "ConditionalCheckFailedRequests"
         statType: "ave"
         delta: false
         multiplier: 1
         aggregationType: "AVERAGE"
         timeRollUpType: "AVERAGE"
         clusterRollUpType: "INDIVIDUAL"
    metricsTimeRange:
      startTimeInMinsBeforeNow: 5
      endTimeInMinsBeforeNow: 0
    
    getMetricStatisticsRateLimit: 400

    maxErrorRetrySize: 0
~~~

## Metrics
Typical metric path: **Application Infrastructure Performance|\<Tier\>|Custom Metrics|Amazon DynamoDB|\<Account Name\>|\<Region\>|Table Name|\<table name\>** followed by the metrics defined in the link below:

- [DynamoDB Metrics](http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/dynamo-metricscollected.html)

## Credentials Encryption
Please visit [this page](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-Password-Encryption-with-Extensions/ta-p/29397) to get detailed instructions on password encryption. The steps in this document will guide you through the whole process.

## Extensions Workbench
Workbench is an inbuilt feature provided with each extension in order to assist you to fine tune the extension setup before you actually deploy it on the controller. Please review the following document on [How to use the Extensions WorkBench](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-the-Extensions-WorkBench/ta-p/30130)

## Troubleshooting
Please follow the steps listed in this [troubleshooting-document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. These are a set of common issues that customers might have faced during the installation of the extension.

## Contributing
Always feel free to fork and contribute any changes directly here on [GitHub](https://github.com/Appdynamics/aws-dynamodb-monitoring-extension).

## Version
   |          Name            |  Version   |
   |--------------------------|------------|
   |Extension Version         |2.0.5      |
   |Last Update               |04/06/2021 |
   |Change List|[ChangeLog](https://github.com/Appdynamics/aws-dynamodb-monitoring-extension/blob/master/CHANGELOG.md)|

**Note**: While extensions are maintained and supported by customers under the open-source licensing model, they interact with agents and Controllers that are subject to [AppDynamics’ maintenance and support policy](https://docs.appdynamics.com/latest/en/product-and-release-announcements/maintenance-support-for-software-versions). Some extensions have been tested with AppDynamics 4.5.13+ artifacts, but you are strongly recommended against using versions that are no longer supported.
