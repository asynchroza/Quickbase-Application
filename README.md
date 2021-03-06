<h3>Quickbase Application</h3>
<hr/>
<p> In order to run the program, you have to execute the <strong>QBApplication-Maven-1.0.SNAPSHOT-jar-with-dependencies.jar</strong> which could be found in the <strong>target</strong> directory. </p>
<a href="https://ibb.co/v4kX14x"><img src="https://i.ibb.co/d4DmB40/freshdesk.png" alt="freshdesk" border="0"></a>

<hr/>
<p> To run the tests, you need to enter <strong><em>mvn test</em></strong> in the root directory </p>
<a href="https://ibb.co/ZKWJXdN"><img src="https://i.ibb.co/q9knjBx/image.png" alt="image" border="0"></a>

<hr/>
<p> <strong> NB! </strong> Project is built to be run with <strong>JDK 11</strong> (Rolled back from JDK 17 for better compatibility with Maven versions) and <strong>Maven 3.6.3</strong></p>
<p> Essentially, the easiest way to run the tests would be by using IntelliJ and the Lifecycle tasks found in the Maven option menu</p>
<hr/>
 <li> Tests are made for the most basic edge cases which can be encountered during input and the running process of the program. This is because, in order to achieve more advanced tests, environmental variables (GITHUB_TOKEN and FRESHDESK_TOKEN) should be passed as secrets and unpriviliged repository clones will fail them during building phases </li>
<hr/>
<p><strong> If you have any inquries feel free to email me! </strong></p>
