# Note for shader usage

## In variables

The following three variables are available if you register your program with a model.

```GLSL
layout(location=0) in vec3 vs_in_position;
layout(location=1) in vec2 vs_in_uv;
layout(location=2) in vec3 vs_in_normal;
```

|    Variable    |   Explanation   |
| :------------: | :-------------: |
| vs_in_position | vertex position |
|    vs_in_uv    |    vertex uv    |
|  vs_in_normal  |  vertex normal  |

Use *Model3D.AddShader()* to add a program to a model.
Below is an example to register a program for spotlighting.

```java
Model3D.RemoveAllShaders(model_handle);//Remove the default program.
Model3D.AddShader(model_handle, "spotlight");//Add a new program.
```

The "spotlight" program has to be created beforehand.

```Java
//Create a program.
GLShaderFunctions.CreateProgram(
	"spotlight", 
	"./Data/Shader/330/three_spotlights_phong/vshader.glsl",
	"./Data/Shader/330/three_spotlights_phong/fshader.glsl");
```

## Uniform variables

The following six variables are available if you register your shader with CameraFront.

```GLSL
uniform vec3 camera_position;
uniform vec3 camera_target;
uniform mat4 projection;
uniform mat4 view_transformation;
uniform float camera_near;
uniform float camera_far;
```

|      Variable       |                   Explanation                    |
| :-----------------: | :----------------------------------------------: |
|   camera_position   |              position of the camera              |
|    camera_target    |      normalized target vector of the camera      |
|     projection      | projection matrix (such as a perspective matrix) |
| view_transformation |            view transformation matrix            |
|     camera_near     |             near value of the camera             |
|     camera_far      |             far value of the camera              |

Use CameraFront.AddUserShader() to add a shader to the camera.
Below is an example to register a program for spotlighting.

```Java
CameraFront.AddUserShader("spotlight");
```

## Set uniform variables

Transmission of uniform data usually takes place in *Update()*.

Here is an illustrative snippet.
See [SingleSpotlightTestWindow.java](https://github.com/Dabasan/JOGLFramework2Samples/blob/master/src/com/daxie/testspace/joglf/v2/spotlighting/SingleSpotlightTestWindow.java) for a complete example that actually works.

```Java
private ShaderProgram program=new ShaderProgram("spotlight");

@Override
protected void Update() {
    program.Enable();//Enable the "spotlight" program.
    
    //Set uniform variables.
    program.SetUniform("light_position", light_position);
    program.SetUniform("light_direction", light_direction);
    program.SetUniform("light_attenuation", light_attenuation);
    program.SetUniform("phi", phi);
    program.SetUniform("theta", theta);
    program.SetUniform("falloff", falloff);
    program.SetUniform("diffuse_color", diffuse_color);
    program.SetUniform("ambient_color", ambient_color);
    program.SetUniform("specular_color", specular_power);
}
```

