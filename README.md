# Norma

A tool to convert a variety of inputs into normalized, tagged, XHTML (with embedded/linked SVG and PNG where
appropriate). The initial emphasis is on scholarly publications but much of the technology is general.

## Universal Search Tool (Citizen STEM Search; AMI-STEM)

This is a bundle of all `norma` and `ami` functionality to transform PDFs and XML into structured semantic HTML. It's alpha (2018-09) and we have 4-6 testers each with different projects.

## Installation

For a simple introduction and a description of how to install binaries of the software please see: [here](http://contentmine.github.io)



## Input

Norma will convert legacy files into scholarly html. It converts files that are in a CProject structure. This enables it
to process multiple papers in a single run without overwriting files. It also keeps all the data from each paper together
in its own CTree. This includes metadata about the paper, images that may have been extracted from the paper and
supplementary files such as tables.

To convert a CTree full of NLM xml files such as those you might have downloaded from EuropePMC with getpapers you can run:
  ```
  norma --project <CProject folder> --input fulltext.xml --output scholarly.html --transform nlm2html
  ```

# Building from source

Norma can be built with maven3 and requires java 1.7 (sometimes 1.8) or greater.

## Contributing to development
If you're interested in contributing please take a look at: [CONTRIBUTING.md](CONTRIBUTING.md)
