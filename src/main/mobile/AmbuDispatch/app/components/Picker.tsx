import RNPickerSelect from 'react-native-picker-select';

interface inputs {
  label: string;
  value: string;
  color: string;
}

export default function Picker({
  placeholder,
  labels,
  onChange,
}: {
  placeholder: string;
  labels: inputs[];
  onChange: (value: string) => void;
}) {
  return (
    <RNPickerSelect
      style={{
        inputIOSContainer: {
          backgroundColor: 'rgb(64 64 64)',
          borderColor: 'rgb(82 82 82)',
          borderStyle: 'solid',
          borderWidth: 1,
          padding: 10,
          width: '100%',
          borderRadius: 10,
        },
      }}
      placeholder={{label: placeholder, value: null}}
      darkTheme={true}
      textInputProps={{color: 'white', textSize: '40px'} as any}
      onValueChange={value => onChange(value)}
      items={labels}
    />
  );
}
