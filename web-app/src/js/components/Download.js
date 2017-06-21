import React, { Component } from 'react';

import Box from 'grommet/components/Box';
import Section from 'grommet/components/Section';

class Download extends Component {

  componentWillMount() {
    const helpUrl = window.baseUrl + "/download";
    window.open(helpUrl);
  }
  
  render() {

    return (
      <Box>
        <Section>
          <Box  alignSelf="center" pad={{vertical: 'large'}}>
            <h2>Download Andon System android Application</h2>
            <h3 style={{textAlign: 'center'}}><a href={window.baseUrl + "/download"}>Download</a></h3>
          </Box>
        </Section>
      </Box>
    );
  }
}

export default Download;
