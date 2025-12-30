# react-native-clone-detector

React Native Android module to detect cloned or parallel app environments and return a clone risk score.

## Usage


```js
import { DetectClone } from 'react-native-clone-detector';

async function checkCloneStatus() {
  try {
    const result = await DetectClone();

    console.log(result.isCloned); // boolean
    console.log(result.score);    // number
    console.log(result.signals);  // detailed signals
  } catch (error) {
    console.error('Clone detection failed:', error);
  }
}

```


## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
