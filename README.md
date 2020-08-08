# cz-birth-code-manager

The REST API providing operations with CZ birth codes.

## Generate random CZ birth code
[https://tools.jjaros.cz/cz-birth-code/generate/random](https://tools.jjaros.cz/cz-birth-code/generate/random)

## Generate CZ birth code by required birth date
[https://tools.jjaros.cz/cz-birth-code/generate/19700101](https://tools.jjaros.cz/cz-birth-code/generate/19700101)

*Note:* Put your required value as path variable instead of value "19700101" from example URI. Use the Date in format **yyyyMMdd**.

## Application on localhost

**1.  Pull the repository**

Move to the `cz-birth-code-manager` directory and run:
```
git clone https://github.com/jjaros/cz-birth-code-manager.git
```


**2.  Run the Application**

Move to the `cz-birth-code-manager` directory and run:
```
lein run
```
*Note:* See the terminal output for URI to App endpoint.


**3.  Run the Unit tests**

Move to the `cz-birth-code-manager` directory and run:
```
lein test
```

...or automatically refreshed tests (see [lein-test-refresh](https://github.com/jakemcc/lein-test-refresh))
```
lein test-refresh
```
