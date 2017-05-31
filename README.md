# What is this?
Some sequential pattern mining algorithms I have written in Java. Both the source and a built application are provided for your usage.
# What is sequential pattern mining
In laymen's terms, sequential pattern mining is the process of finding frequently occuring sub-sequences from a set of sequences. For a formal definition see [SPMF](http://www.philippe-fournier-viger.com/spmf/index.php?link=documentation.php#examplePrefixSpan). In this module the input sequences are simply a text file in the SPMF format, like:

Sequences | 
------------ | 
1 -1 3 -1 7 -2 | 
3 -1 1 -1 3 -1 7 -1 1 -2 |
7 -1 3 -1 1 -1 3 -1 7 -2 |

As you can see each sequence it made up of `items`, which are delimited by `-1` and terminated by `-2`. If we mined the above sequence database using a minimum support of 3, we'd find the sequential patterns: `{1,3,7}, {1,3}, {1,7}, {3,7}, {1}, {3}, {7}`. 

# Our Motivation

Even in this small example you can see plenty of redundancy in the pattern output. Thus, various pattern closures such as max-patterns and closed patterns have been proposed to reduce the pattern output. Briefly, max patterns are sequential patterns that have no super-pattern in the output and closed patterns are sequential patterns with no super-pattern in the output that has the same support. Closed-patterns are said to be *lossless* because they can be used to recover the full set of sequential patterns, whereas max-patterns, although achiveing a generally better reduction, cannot recover the full set of sequential patterns. For a more formal explanation of closed and max patterns see [this slide](http://www.slideshare.net/JustinCletus/mining-frequent-patterns-association-and-correlations/8). 

If we consider these two sequential patterns:
`{1,3,7,7,3,1,42}`
`{1,3,7,7,3,1,99}`
They are perfectly legal max-patterns because neither is the super-pattern of the other, however, they express mostly the same information. In massive and pattern dense sequence databases even max-pattern sequential pattern mining algorithms can produce huge pattern outputs - too large to interpret or visualise even. Surely, there is some way to mine patterns without producing a massive, mostly redundant, set of sequential patterns?

# DC-SPAN
DC-SPAN is our solution to produce low redundancy sequential pattern output. The trade-off for redundancy-controlled pattern output is it is purposefully a *lossy* sequential pattern mining technique.

The white-paper describing DC-SPAN is here (coming soon...pm me if I forget to update this). 

# CC-Span
CC-Span is sequential pattern mining algorithm that mines closed-contiguous sequential patterns. It was introduced by Zhang et al., see [here](http://www.sciencedirect.com/science/article/pii/S0950705115002324). We implemented CC-Span from their paper and used Tries to speed up sub-sequence checking.

# Using this work in your research
For use of DC-SPAN please cite:
```
@article{Bermingham2016,
    title = "Mining Distinct and Contiguous Sequential Patterns From Large Vehicle Trajectories",
    journal = "In Review",
    pages = "1 - 15",
    year = "2016",
    author = "Bermingham, Luke and Lee, Ickjai",
    keywords = "Data mining",
    keywords = "Sequential pattern mining",
    keywords = "Large sequence databases"
}
```

For use of CC-Span please cite:
```
@article{Zhang20151,
    title = "CCSpan: Mining closed contiguous sequential patterns ",
    journal = "Knowledge-Based Systems",
    volume = "89",
    pages = "1 - 13",
    year = "2015",
    issn = "0950-7051",
    doi = "http://dx.doi.org/10.1016/j.knosys.2015.06.014",
    url = "http://www.sciencedirect.com/science/article/pii/S0950705115002324",
    author = "Jingsong Zhang and Yinglin Wang and Dingyu Yang",
    keywords = "Data mining",
    keywords = "Sequential pattern mining",
    keywords = "Closed sequential pattern",
    keywords = "Contiguous constraint",
    keywords = "Closed contiguous sequential pattern"
}
```
# Using the application

1. Download the latest "fat" binary from: [ ![Download](https://api.bintray.com/packages/lukehb/137-SPM/137-SPM/images/download.svg) ](https://bintray.com/lukehb/137-SPM/137-SPM/_latestVersion)
2. Run the "fat" jar by opening a terminal and executing something like `java -jar spm-0.0.1-fat.jar`
3. Type `lc` to list the available commands
4. Run a specific command, for example, Miner: `graspminer -s 10 -g 1 -i input.spmf -o output.spmf`

# Working with the source code
The source is licensed under the MIT licsense so feel free to use it in your projects. It does have some dependencies which are listed in the build.gradle file. The easiest use-case is setting the source up as a gradle project and letting gradle grab those dependencies for you. Next easiest is maven, though you will have translate the dependencies yourself. 

Without cloning, the built source is also hosted on BinTray and can be used in your gradle project like so:

```groovy
repositories {
    maven{url 'https://dl.bintray.com/lukehb/137-SPM'} //hosted on bintray
}

dependencies {
    compile 'onethreeseven:spm:0.0.5'
}
```
