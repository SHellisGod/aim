import React, { useState } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  View,
  Button,
  FlatList,
  Alert,
} from 'react-native';
import XLSX from 'xlsx';
import RNFS from 'react-native-fs';

const App = () => {
  const [data, setData] = useState([]); // Stores financial data
  const [newEntry, setNewEntry] = useState({}); // Stores new entry input

  // Path to save the Excel file
  const filePath = `${RNFS.DocumentDirectoryPath}/FinancialDashboard.xlsx`;

  // Load the Excel file
  const loadExcel = async () => {
    try {
      const fileExists = await RNFS.exists(filePath);
      if (!fileExists) {
        Alert.alert('No data file found. Starting fresh.');
        return;
      }
      const fileData = await RNFS.readFile(filePath, 'base64');
      const workbook = XLSX.read(fileData, { type: 'base64' });
      const sheetName = workbook.SheetNames[0];
      const sheetData = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);
      setData(sheetData);
    } catch (error) {
      console.error('Error loading Excel:', error);
    }
  };

  // Save the Excel file
  const saveExcel = async () => {
    try {
      const worksheet = XLSX.utils.json_to_sheet(data);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'FinancialData');
      const output = XLSX.write(workbook, { type: 'base64', bookType: 'xlsx' });
      await RNFS.writeFile(filePath, output, 'base64');
      Alert.alert('Data saved successfully!');
    } catch (error) {
      console.error('Error saving Excel:', error);
    }
  };

  // Add a new entry
  const addEntry = () => {
    if (!newEntry.Date || !newEntry.BankBalance) {
      Alert.alert('Please fill in all required fields.');
      return;
    }
    setData([...data, newEntry]);
    setNewEntry({});
  };

  // Render each row
  const renderItem = ({ item }) => (
    <View style={styles.row}>
      <Text style={styles.cell}>{item.Date}</Text>
      <Text style={styles.cell}>{item.BankBalance}</Text>
      <Text style={styles.cell}>{item.Cleared}</Text>
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Financial Dashboard</Text>

      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          placeholder="Date (YYYY-MM-DD)"
          value={newEntry.Date || ''}
          onChangeText={(text) => setNewEntry({ ...newEntry, Date: text })}
        />
        <TextInput
          style={styles.input}
          placeholder="Bank Balance"
          keyboardType="numeric"
          value={newEntry.BankBalance || ''}
          onChangeText={(text) => setNewEntry({ ...newEntry, BankBalance: text })}
        />
        <TextInput
          style={styles.input}
          placeholder="Cleared"
          keyboardType="numeric"
          value={newEntry.Cleared || ''}
          onChangeText={(text) => setNewEntry({ ...newEntry, Cleared: text })}
        />
        <Button title="Add Entry" onPress={addEntry} />
      </View>

      <FlatList
        data={data}
        keyExtractor={(item, index) => index.toString()}
        renderItem={renderItem}
      />

      <View style={styles.buttonContainer}>
        <Button title="Load Data" onPress={loadExcel} />
        <Button title="Save Data" onPress={saveExcel} />
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  inputContainer: {
    marginBottom: 20,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    padding: 10,
    marginBottom: 10,
    borderRadius: 5,
  },
  row: {
    flexDirection: 'row',
    marginBottom: 10,
  },
  cell: {
    flex: 1,
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  buttonContainer: {
    marginTop: 20,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
});

export default App;
