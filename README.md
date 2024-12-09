This plugine, add a shortcut button in elcipse toolbar, on clicking, it searches for keywords in java code in eclipse code and automatically add breakpoints there. Helpful to debug or inspect code. Breakpoints can be added:
1. based on keywords (required)
2. based on class name (optional)
3. based on package name (optional)

Please add a file bpa.json in your user home directory. i.e
```
{
  "keywords": ["afzal", "exec"],
  "packages": [],
  "javaclasses":[]
}
```