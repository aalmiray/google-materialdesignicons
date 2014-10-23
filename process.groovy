@Grab('commons-io:commons-io:2.4')
import org.apache.commons.io.FileUtils

File root = new File('material-design-icons-1.0.0')
new File(root, 'sprites').delete()
File subprojects = new File('subprojects')

Map matrix = [
    '1x_ios': [24, 48],
    '1x_web': [18, 24, 36, 48],
    '2x_ios': [24, 48],
    '2x_web': [18, 24, 36, 48],
    '3x_ios': [24, 48],
    'drawable-hdpi': [18, 24, 36, 48],
    'drawable-mdpi': [18, 24, 36, 48],
    'drawable-xhdpi': [18, 24, 36, 48],
    'drawable-xxhdpi': [18, 24, 36, 48],
    'drawable-xxxhdpi': [18, 24, 36, 48],
    'svg': [24, 48],
]

settings = []
matrix.each { category, sizes ->
    sizes.each { size ->
        String projectName = "${category}-${size}"
        settings << "include 'subprojects/$projectName'"
        File project = new File(subprojects, "${projectName}/src/main/resources/com/google/materialdesignicons/")
        project.mkdirs()
        List index = []
        root.eachDir { srcdir ->
            File destdir = new File(project, srcdir.name)
            destdir.mkdirs()
            File srcCategoryDir = new File(srcdir, category)
            File destCategoryDir = new File(destdir, category)
            srcCategoryDir.eachFileMatch({ it =~ /ic_.*${size}.{6}/ }) { f ->
                FileUtils.copyFile(f, new File(destCategoryDir, f.name))
                index << "${f.parentFile.parentFile.name}/${f.name}"
            }
        }
        File indexFile = new File(subprojects, "${projectName}/src/main/resources/META-INF/icon-index.txt")
        indexFile.parentFile.mkdirs()
        indexFile.text = index.join('\n')
        FileUtils.copyFile(new File(root, 'LICENSE'), new File(indexFile.parentFile, 'LICENSE'))
    }
}

new File('settings.gradle').text = settings.join('\n') + '''

rootProject.name = 'materialdesign'
rootProject.children.each { project ->
    int slash = project.name.indexOf('/')
    String fileBaseName = project.name[(slash+1)..-1]
    String projectDirName = project.name
    project.name = fileBaseName
    project.projectDir = new File(settingsDir, projectDirName)
    assert project.projectDir.isDirectory()
}

'''